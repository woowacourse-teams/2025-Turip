package com.on.turip.di

import android.content.Context
import timber.log.Timber

object ApplicationContextProvider {
    private const val ERROR_CONTEXT_IS_NULL: String = "현재 context 는 null 입니다."
    private var appContext: Context? = null
    val applicationContext: Context
        get() =
            appContext ?: run {
                Timber.e(ERROR_CONTEXT_IS_NULL)
                throw IllegalArgumentException(ERROR_CONTEXT_IS_NULL)
            }

    fun setupApplicationContext(context: Context) {
        this.appContext = context.applicationContext
    }
}
