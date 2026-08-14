package com.on.turip.feature.splash.impl.di

import com.on.turip.core.common.deeplink.InvitationNavigationCoordinator
import com.on.turip.core.common.deeplink.StartupNavigationArbiter
import com.on.turip.core.navigation.NavKeyProvider
import com.on.turip.feature.splash.impl.SplashViewModel
import com.on.turip.feature.splash.impl.navigation.SplashNavKeyProvider
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val splashModule = module {
    // startup-수명 가드. 프로세스당 1회 실행되는 Splash 특성상 single로 공유한다.
    // (MainApp의 딥링크 Collector와 Splash 세션 진입이 동일 인스턴스를 사용해야 함)
    single { StartupNavigationArbiter() }
    single { InvitationNavigationCoordinator() }
    single { SplashNavKeyProvider(get()) } bind NavKeyProvider::class
    viewModel {
        SplashViewModel(get(), get(), get(), get())
    }
}
