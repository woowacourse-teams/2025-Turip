package com.jetbrains.kmpapp.di

import com.on.main.di.mainModule
import com.on.turip.BuildKonfig
import com.on.turip.core.data.di.dataModule
import com.on.turip.core.local.di.localModule
import com.on.turip.core.network.di.datasourceModule
import com.on.turip.core.network.di.networkModule
import com.on.turip.core.network.di.serviceModule
import com.on.turip.feature.bookmark.impl.di.bookmarkModule
import com.on.turip.feature.home.impl.di.homeModule
import com.on.turip.feature.search.impl.di.searchModule
import com.on.turip.feature.trip.impl.di.tripModule
import com.on.turip.feature.turip.impl.di.myTuripModule
import com.on.turip.feature.turipdetail.impl.di.turipDetailModule
import com.on.turip.feature.invitation.impl.di.invitationModule
import com.on.turip.feature.login.impl.di.loginModule
import com.on.turip.feature.main.navigation.SavedStateConfigurationProvider
import com.on.turip.feature.mypage.impl.di.myPageModule
import com.on.turip.feature.splash.impl.di.splashModule
import org.koin.dsl.module

val coreModule =
    module {
        includes(
            dataModule,
            localModule,
            networkModule(
                baseUrl = BuildKonfig.BASE_URL,
                isDebug = BuildKonfig.IS_DEBUG,
            ),
            serviceModule,
            datasourceModule(BuildKonfig.BASE_URL),
            mainModule,
        )
    }

val featureModule =
    module {
        includes(
            splashModule,
            loginModule,
            invitationModule,
            homeModule,
            bookmarkModule,
            myPageModule,
            searchModule,
            myTuripModule,
            turipDetailModule,
            tripModule,
        )
    }

val appModule =
    module {
        includes(coreModule, featureModule)
        single { SavedStateConfigurationProvider(getAll()) }
    }
