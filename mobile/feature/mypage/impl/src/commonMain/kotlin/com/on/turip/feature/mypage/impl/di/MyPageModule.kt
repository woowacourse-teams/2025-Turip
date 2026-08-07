package com.on.turip.feature.mypage.impl.di

import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.mypage.impl.MyPageViewModel
import com.on.turip.feature.mypage.impl.navigation.MyPageNavKeyProvider
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val myPageModule = module {
    single { MyPageNavKeyProvider() } bind NavKeyProvider::class
    viewModel<MyPageViewModel> { MyPageViewModel(get(), get(), get(), get(), get()) }
}
