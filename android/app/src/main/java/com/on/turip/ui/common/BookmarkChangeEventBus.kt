package com.on.turip.ui.common

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkChangeEventBus @Inject constructor() {
    private val _changes =
        MutableSharedFlow<Unit>(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    val changes: SharedFlow<Unit> = _changes.asSharedFlow()

    fun notifyChanged() {
        _changes.tryEmit(Unit)
    }
}
