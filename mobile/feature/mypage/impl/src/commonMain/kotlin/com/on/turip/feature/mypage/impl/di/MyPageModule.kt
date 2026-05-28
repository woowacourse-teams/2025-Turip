package com.on.turip.feature.mypage.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.mypage.impl.navigation.MyPageNavKeyProvider
import com.on.turip.feature.mypage.impl.viewmodel.MyPageViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val myPageModule = module {
    viewModel { MyPageViewModel(get(), get(), get()) }
    single { MyPageNavKeyProvider() } bind NavKeyProvider::class
}
