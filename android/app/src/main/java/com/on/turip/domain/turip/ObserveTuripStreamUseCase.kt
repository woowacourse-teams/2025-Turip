package com.on.turip.domain.turip

import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.TuripResult
import com.on.turip.domain.turip.repository.TuripRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
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
            while (true) {
                var streamResult: TuripResult<Unit>? = null

                heartbeatManager.prepareNextAttempt()

                val currentAttemptJob =
                    launch {
                        turipRepository
                            .streamTuripEvents(turipId)
                            .collect { eventResult ->
                                when (eventResult) {
                                    is TuripResult.Success -> {
                                        send(TuripStreamResult.Event(eventResult.value))
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
                heartbeatManager.stop()
                heartbeatManager.clearTimedOutState()

                when {
                    heartbeatManager.isTimedOut -> {
                        Timber.w("튜립 SSE 하트비트 타임아웃으로 재연결합니다. turipId=%s", turipId)
                    }

                    streamResult is TuripResult.Failure -> {
                        val errorType = (streamResult as TuripResult.Failure).errorType
                        val fatal = mapToFatalOrNull(errorType) ?: return@channelFlow
                        send(fatal)
                        return@channelFlow
                    }
                }

                send(TuripStreamResult.Reconnecting)
                delay(TuripStreamHeartbeatManager.STREAM_RECONNECT_DELAY_MILLIS)
            }
        }

    private fun mapToFatalOrNull(errorType: ErrorType): TuripStreamResult.Fatal? =
        when (errorType) {
            ErrorType.Auth.TokenExpired -> TuripStreamResult.Fatal.TokenExpired
            ErrorType.Auth.Forbidden -> TuripStreamResult.Fatal.Forbidden
            else -> null
        }
}
