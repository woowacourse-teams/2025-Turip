package com.on.turip.core.local.di

import com.on.turip.core.data.datasource.DeferredDeepLinkLocalDataSource
import com.on.turip.core.data.datasource.NotificationSettingLocalDataSource
import com.on.turip.core.data.datasource.SearchHistoryDataSource
import com.on.turip.core.data.datasource.UserStorageLocalDataSource
import com.on.turip.core.domain.fcm.FcmTokenProvider
import com.on.turip.core.domain.fid.DeviceFidManager
import com.on.turip.core.local.datasourceimpl.DefaultDeferredDeepLinkLocalDataSource
import com.on.turip.core.local.datasourceimpl.DefaultNotificationSettingLocalDataSource
import com.on.turip.core.local.datasourceimpl.DefaultSearchHistoryDataSource
import com.on.turip.core.local.datasourceimpl.DefaultUserStorageLocalDataSource
import com.on.turip.core.local.fcm.DefaultFcmTokenProvider
import com.on.turip.core.local.fid.DefaultDeviceFidManager
import org.koin.dsl.module

val localModule = module {
    single<UserStorageLocalDataSource> { DefaultUserStorageLocalDataSource(get()) }
    single<DeferredDeepLinkLocalDataSource> { DefaultDeferredDeepLinkLocalDataSource(get()) }
    single<SearchHistoryDataSource> { DefaultSearchHistoryDataSource(get()) }
    single<DeviceFidManager> { DefaultDeviceFidManager(get()) }
    single<NotificationSettingLocalDataSource> { DefaultNotificationSettingLocalDataSource(get()) }
    single<FcmTokenProvider> { DefaultFcmTokenProvider() }
}
