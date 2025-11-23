package com.on.turip.di

import com.on.turip.data.login.repository.DefaultLoginRepository
import com.on.turip.domain.login.LoginRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class RepositoryModule2 {
    @Binds
    abstract fun bindLoginRepository(defaultLoginRepository: DefaultLoginRepository): LoginRepository
}
