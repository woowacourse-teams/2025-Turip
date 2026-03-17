package com.on.turip.domain.turip

import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TuripStreamHeartbeatManager @Inject constructor() {
    private var heartbeatTimer: Job? = null

    @Volatile
    private var _isTimedOut: Boolean = false

    val isTimedOut: Boolean
        get() = _isTimedOut

    fun onHeartbeat(
        scope: CoroutineScope,
        onTimeout: () -> Unit,
    ) {
        reset(scope, onTimeout)
    }

    fun prepareNextAttempt() {
        _isTimedOut = false
    }

    suspend fun stop() {
        heartbeatTimer?.cancelAndJoin()
        heartbeatTimer = null
    }

    fun clearTimedOutState() {
        _isTimedOut = false
    }

    private fun reset(
        scope: CoroutineScope,
        onTimeout: () -> Unit,
    ) {
        heartbeatTimer?.cancel()
        heartbeatTimer =
            scope.launch {
                delay(HEARTBEAT_TIMEOUT_MILLIS)
                _isTimedOut = true
                onTimeout()
            }
    }

    companion object {
        const val STREAM_RECONNECT_DELAY_MILLIS = 3_000L
        const val HEARTBEAT_TIMEOUT_MILLIS = 30_000L
    }
}
