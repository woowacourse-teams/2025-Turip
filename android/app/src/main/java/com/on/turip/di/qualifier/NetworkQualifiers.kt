package com.on.turip.di.qualifier

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultKtorfit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NoAuthKtorfit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NoAuthHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SseHttpClient
