package com.on.turip.domain.turip

import com.on.turip.core.result.ErrorType
import com.on.turip.domain.turip.repository.TuripRepository
import com.on.turip.domain.turip.result.TuripStreamResult
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ObserveTuripStreamUseCase @Inject constructor(
    private val turipRepository: TuripRepository,
    private val heartbeatManager: TuripStreamHeartbeatManager,
) {
    operator fun invoke(turipId: Long): Flow<TuripStreamResult> =
        channelFlow {
            var retryCount = 0

            while (true) {
                var streamFailure: TuripStreamResult.Fatal? = null
                var timedOut = false

                heartbeatManager.start(this)

                val currentAttemptJob =
                    launch {
                        turipRepository
                            .streamTuripEvents(turipId)
                            .collect { eventResult ->
                                when (eventResult) {
                                    is TuripStreamResult.Event -> {
                                        val event = eventResult.event
                                        if (event is TuripStreamEvent.Connect || event is TuripStreamEvent.Heartbeat) {
                                            if (event is TuripStreamEvent.Connect) retryCount = 0
                                            heartbeatManager.onHeartbeat()
                                        }
                                        send(TuripStreamResult.Event(event))
                                    }

                                    is TuripStreamResult.Fatal -> {
                                        streamFailure = eventResult
                                    }

                                    is TuripStreamResult.Reconnecting -> {
                                        Unit
                                    }
                                }
                            }
                    }

                val timeoutObserveJob =
                    heartbeatManager.timeoutEvent
                        .onEach {
                            timedOut = true
                            currentAttemptJob.cancel()
                        }.launchIn(this)

                currentAttemptJob.join()
                timeoutObserveJob.cancel()
                heartbeatManager.stop()

                if (shouldExitStream(timedOut, turipId, streamFailure)) return@channelFlow

                val isErrorRetry =
                    streamFailure is TuripStreamResult.Fatal && !timedOut

                if (isErrorRetry && retryCount >= MAX_RETRY_EXPONENT) {
                    Timber.e(
                        "튜립 SSE 최대 재시도 횟수(%d) 초과. 스트림 종료. turipId=%s",
                        MAX_RETRY_EXPONENT,
                        turipId,
                    )
                    val errorType =
                        (streamFailure as? TuripStreamResult.Fatal.ConnectionLost)?.errorType
                            ?: ErrorType.Unknown
                    send(TuripStreamResult.Fatal.ConnectionLost(errorType))
                    return@channelFlow
                }

                send(TuripStreamResult.Reconnecting(retryCount))

                // 에러로 인한 재시도: Exponential Backoff (3s → 6s → 12s → 24s → 48s)
                // 하트비트 타임아웃 또는 정상 종료: 고정 3s (연결이 건강했으므로 빠르게 재연결)
                val reconnectDelay =
                    if (isErrorRetry) {
                        val backoff =
                            minOf(INITIAL_RETRY_DELAY_MILLIS shl retryCount, MAX_RETRY_DELAY_MILLIS)
                        Timber.d(
                            "튜립 SSE 에러 재연결 대기. delay=%dms, retryCount=%d, turipId=%s",
                            backoff,
                            retryCount,
                            turipId,
                        )
                        retryCount++
                        backoff
                    } else {
                        TuripStreamHeartbeatManager.STREAM_RECONNECT_DELAY_MILLIS
                    }

                delay(reconnectDelay)
            }
        }

    private suspend fun ProducerScope<TuripStreamResult>.shouldExitStream(
        timedOut: Boolean,
        turipId: Long,
        streamFailure: TuripStreamResult.Fatal?,
    ): Boolean {
        when {
            timedOut -> {
                Timber.d("튜립 SSE 하트비트 타임아웃으로 재연결합니다. turipId=%s", turipId)
            }

            streamFailure != null -> {
                Timber.e(
                    "튜립 SSE 스트림 실패. turipId=%s, failure=%s",
                    turipId,
                    streamFailure::class.simpleName,
                )
                when (streamFailure) {
                    TuripStreamResult.Fatal.TokenExpired -> {
                        send(TuripStreamResult.Fatal.TokenExpired)
                        return true
                    }

                    TuripStreamResult.Fatal.Forbidden -> {
                        send(TuripStreamResult.Fatal.Forbidden)
                        return true
                    }

                    is TuripStreamResult.Fatal.ConnectionLost -> {
                        if (!isRetryableConnectionError(streamFailure.errorType, turipId)) {
                            send(TuripStreamResult.Fatal.ConnectionLost(streamFailure.errorType))
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    private fun isRetryableConnectionError(
        errorType: ErrorType,
        turipId: Long,
    ): Boolean =
        when (errorType) {
            ErrorType.Network,
            ErrorType.Unknown,
            -> {
                true
            }

            else -> {
                Timber.e("튜립 SSE 에러로 스트림을 중단합니다. turipId=%s, errorType=%s", turipId, errorType)
                false
            }
        }

    companion object {
        private const val INITIAL_RETRY_DELAY_MILLIS = 3_000L
        private const val MAX_RETRY_DELAY_MILLIS = 60_000L
        private const val MAX_RETRY_EXPONENT = 5
    }
}
