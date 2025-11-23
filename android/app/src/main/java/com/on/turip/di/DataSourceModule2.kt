package com.on.turip.di

import com.on.turip.data.login.datasource.DefaultThirdPartyLoginRemoteDatasource
import com.on.turip.data.login.datasource.ThirdPartyLoginRemoteDatasource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class DataSourceModule2 {
    @Binds
    abstract fun bindThirdPartyLoginRemoteDatasource(
        defaultThirdPartyLoginRemoteDatasource: DefaultThirdPartyLoginRemoteDatasource,
    ): ThirdPartyLoginRemoteDatasource
}
