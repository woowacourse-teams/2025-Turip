package com.on.turip.domain.turip

import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.TuripResult
import com.on.turip.domain.turip.repository.TuripRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed interface TuripStreamResult {
    data class Event(
        val event: TuripStreamEvent,
    ) : TuripStreamResult

    data object Reconnecting : TuripStreamResult

    sealed interface Fatal : TuripStreamResult {
        data object TokenExpired : Fatal

        data object Forbidden : Fatal
    }
}

class ObserveTuripStreamUseCase @Inject constructor(
    private val turipRepository: TuripRepository,
    private val heartbeatManager: TuripStreamHeartbeatManager,
) {
    operator fun invoke(turipId: Long): Flow<TuripStreamResult> =
        channelFlow {
            var retryCount = 0

            while (true) {
                var streamResult: TuripResult<Unit>? = null

                heartbeatManager.prepareNextAttempt()

                val currentAttemptJob =
                    launch {
                        val self = coroutineContext.job
                        turipRepository
                            .streamTuripEvents(turipId)
                            .collect { eventResult ->
                                when (eventResult) {
                                    is TuripResult.Success -> {
                                        val event = eventResult.value
                                        if (event is TuripStreamEvent.Connect || event is TuripStreamEvent.Heartbeat) {
                                            if (event is TuripStreamEvent.Connect) retryCount = 0
                                            heartbeatManager.onHeartbeat(
                                                scope = this@channelFlow,
                                                onTimeout = { self.cancel() },
                                            )
                                        }
                                        send(TuripStreamResult.Event(event))
                                    }

                                    is TuripResult.Failure -> {
                                        streamResult = eventResult
                                    }
                                }
                            }

                        if (streamResult == null) {
                            streamResult = TuripResult.Success(Unit)
                        }
                    }

                currentAttemptJob.join()
                val timedOut: Boolean = heartbeatManager.isTimedOut
                heartbeatManager.stop()
                heartbeatManager.clearTimedOutState()

                when {
                    timedOut -> {
                        Timber.w("튜립 SSE 하트비트 타임아웃으로 재연결합니다. turipId=%s", turipId)
                    }

                    streamResult is TuripResult.Failure -> {
                        val failure = streamResult as TuripResult.Failure
                        Timber.w(
                            "튜립 SSE 스트림 실패. turipId=%s, errorType=%s, cause=%s",
                            turipId,
                            failure.errorType,
                            failure.cause?.javaClass?.simpleName,
                        )
                        when (handleTuripStreamFailure(failure.errorType, turipId)) {
                            StreamFailureAction.Retry -> {
                                Unit
                            }

                            StreamFailureAction.Stop -> {
                                return@channelFlow
                            }

                            StreamFailureAction.FatalTokenExpired -> {
                                send(TuripStreamResult.Fatal.TokenExpired)
                                return@channelFlow
                            }

                            StreamFailureAction.FatalForbidden -> {
                                send(TuripStreamResult.Fatal.Forbidden)
                                return@channelFlow
                            }
                        }
                    }
                }

                send(TuripStreamResult.Reconnecting)

                // 에러로 인한 재시도: Exponential Backoff (3s → 6s → 12s → 24s → 48s → 최대 60s)
                // 하트비트 타임아웃 또는 정상 종료: 고정 3s (연결이 건강했으므로 빠르게 재연결)
                val reconnectDelay =
                    if (streamResult is TuripResult.Failure && !timedOut) {
                        val backoff =
                            minOf(INITIAL_RETRY_DELAY_MILLIS shl retryCount, MAX_RETRY_DELAY_MILLIS)
                        Timber.d(
                            "튜립 SSE 에러 재연결 대기. delay=%dms, retryCount=%d, turipId=%s",
                            backoff,
                            retryCount,
                            turipId,
                        )
                        retryCount = minOf(retryCount + 1, MAX_RETRY_EXPONENT)
                        backoff
                    } else {
                        TuripStreamHeartbeatManager.STREAM_RECONNECT_DELAY_MILLIS
                    }

                delay(reconnectDelay)
            }
        }

    private fun handleTuripStreamFailure(
        errorType: ErrorType,
        turipId: Long,
    ): StreamFailureAction =
        when (errorType) {
            ErrorType.Auth.TokenExpired -> {
                StreamFailureAction.FatalTokenExpired
            }

            ErrorType.Auth.Forbidden -> {
                Timber.w("튜립 SSE 권한이 없어 스트림을 중단합니다. turipId=%s", turipId)
                StreamFailureAction.FatalForbidden
            }

            ErrorType.Network,
            ErrorType.Unknown,
            -> {
                StreamFailureAction.Retry
            }

            else -> {
                Timber.w("튜립 SSE 에러로 스트림을 중단합니다. turipId=%s, errorType=%s", turipId, errorType)
                StreamFailureAction.Stop
            }
        }

    private enum class StreamFailureAction {
        Retry,
        Stop,
        FatalTokenExpired,
        FatalForbidden,
    }

    companion object {
        private const val INITIAL_RETRY_DELAY_MILLIS = 3_000L
        private const val MAX_RETRY_DELAY_MILLIS = 60_000L
        private const val MAX_RETRY_EXPONENT = 5
    }
}
