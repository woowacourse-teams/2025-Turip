package com.on.turip.di

import com.on.turip.platform.device.AndroidAppEnvironmentInfoProvider
import com.on.turip.platform.device.AppEnvironmentInfoProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppEnvironmentModule {
    @Binds
    @Singleton
    abstract fun provideAppEnvironmentInfoProvider(
        androidAppEnvironmentInfoProvider: AndroidAppEnvironmentInfoProvider,
    ): AppEnvironmentInfoProvider
}
