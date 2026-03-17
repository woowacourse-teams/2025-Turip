package com.on.turip.domain.turip

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class TuripStreamHeartbeatManager @Inject constructor() {
    private var heartbeatTimer: Job? = null
    private var scope: CoroutineScope? = null

    private val _timeoutEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val timeoutEvent: Flow<Unit> = _timeoutEvent.asSharedFlow()

    fun start(scope: CoroutineScope) {
        this.scope = scope
        resetTimer()
    }

    fun onHeartbeat() {
        resetTimer()
    }

    suspend fun stop() {
        heartbeatTimer?.cancelAndJoin()
        heartbeatTimer = null
        scope = null
    }

    private fun resetTimer() {
        heartbeatTimer?.cancel()
        heartbeatTimer =
            scope?.launch {
                delay(HEARTBEAT_TIMEOUT_MILLIS)
                _timeoutEvent.tryEmit(Unit)
            }
    }

    companion object {
        const val STREAM_RECONNECT_DELAY_MILLIS = 3_000L
        const val HEARTBEAT_TIMEOUT_MILLIS = 40_000L
    }
}
