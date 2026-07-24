package com.on.turip.di

import com.on.turip.data.turip.service.DefaultTuripStreamService
import com.on.turip.data.turip.service.TuripStreamService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TuripStreamServiceModule {
    @Binds
    @Singleton
    abstract fun bindTuripStreamService(defaultTuripStreamService: DefaultTuripStreamService): TuripStreamService
}
