package com.on.turip

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

private val iosDeepLinkEvents = MutableSharedFlow<String>(extraBufferCapacity = 1)

fun emitIosDeepLink(url: String) {
    iosDeepLinkEvents.tryEmit(url)
}

fun iosDeepLinkFlow(): Flow<String> = iosDeepLinkEvents
