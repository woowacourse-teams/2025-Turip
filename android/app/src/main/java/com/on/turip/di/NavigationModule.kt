package com.on.turip.di

import com.on.turip.navigation.NavKeyProvider
import com.on.turip.ui.compose.bookmark.navigation.BookMarkNavKeyProvider
import com.on.turip.ui.compose.home.navigation.HomeNavKeyProvider
import com.on.turip.ui.compose.login.navigation.LoginNavKeyProvider
import com.on.turip.ui.compose.mypage.navigation.MyPageNavKeyProvider
import com.on.turip.ui.compose.search.keyword.navigation.SearchNavKeyProvider
import com.on.turip.ui.compose.search.regionresult.navigation.RegionResultNavKeyProvider
import com.on.turip.ui.compose.trip.navigation.TripDetailNavKeyProvider
import com.on.turip.ui.compose.turip.navigation.MyTuripNavKeyProvider
import com.on.turip.ui.compose.turipdetail.navigation.TuripDetailNavKeyProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityComponent::class)
abstract class NavigationModule {
    @Binds
    @IntoSet
    abstract fun bindBookMarkNavKeyProvider(impl: BookMarkNavKeyProvider): NavKeyProvider

    @Binds
    @IntoSet
    abstract fun bindHomeNavKeyProvider(impl: HomeNavKeyProvider): NavKeyProvider

    @Binds
    @IntoSet
    abstract fun bindLoginNavKeyProvider(impl: LoginNavKeyProvider): NavKeyProvider

    @Binds
    @IntoSet
    abstract fun bindMyPageNavKeyProvider(impl: MyPageNavKeyProvider): NavKeyProvider

    @Binds
    @IntoSet
    abstract fun bindSearchNavKeyProvider(impl: SearchNavKeyProvider): NavKeyProvider

    @Binds
    @IntoSet
    abstract fun bindRegionResultNavKeyProvider(impl: RegionResultNavKeyProvider): NavKeyProvider

    @Binds
    @IntoSet
    abstract fun bindTripDetailNavKeyProvider(impl: TripDetailNavKeyProvider): NavKeyProvider

    @Binds
    @IntoSet
    abstract fun bindMyTuripNavKeyProvider(impl: MyTuripNavKeyProvider): NavKeyProvider

    @Binds
    @IntoSet
    abstract fun bindTuripDetailNavKeyProvider(impl: TuripDetailNavKeyProvider): NavKeyProvider

    companion object {
        @Provides
        fun provideNavKeyProviders(providers: Set<@JvmSuppressWildcards NavKeyProvider>): List<@JvmSuppressWildcards NavKeyProvider> =
            providers.toList()
    }
}
