package com.on.turip.feature.mypage.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.mypage.impl.navigation.MyPageNavKeyProvider
import org.koin.dsl.bind
import org.koin.dsl.module

val myPageModule = module {
    single { MyPageNavKeyProvider() } bind NavKeyProvider::class
}
