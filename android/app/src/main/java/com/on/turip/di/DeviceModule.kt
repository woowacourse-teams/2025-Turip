package com.on.turip.di

import com.on.turip.platform.device.AndroidDeviceInfoProvider
import com.on.turip.platform.device.DeviceInfoProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DeviceModule {
    @Binds
    @Singleton
    abstract fun provideDeviceInfoProvider(impl: AndroidDeviceInfoProvider): DeviceInfoProvider
}
