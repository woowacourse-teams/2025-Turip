package com.on.turip.di.qualifier

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultAuthService

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NoAuthAuthService
