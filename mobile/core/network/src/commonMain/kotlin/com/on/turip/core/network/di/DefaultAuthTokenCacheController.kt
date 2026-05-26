package com.on.turip.core.network.di

import com.on.turip.core.domain.session.AuthTokenCacheController

class DefaultAuthTokenCacheController : AuthTokenCacheController {
    @kotlin.concurrent.Volatile
    private var clearCallback: (() -> Unit)? = null

    internal fun onClear(callback: () -> Unit) {
        clearCallback = callback
    }

    override fun clear() {
        clearCallback?.invoke()
    }
}
