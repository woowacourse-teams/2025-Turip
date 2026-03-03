package com.on.turip.ui.compose.mypage.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.navigation.NavKeyProvider
import com.on.turip.navigation.Navigator
import jakarta.inject.Inject
import kotlinx.serialization.modules.PolymorphicModuleBuilder

class MyPageNavKeyProvider @Inject constructor() : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(MyPageNavKey::class, MyPageNavKey.serializer())
    }

    // TODO 네비게이션 적용하고 수정
    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        myPageScreen(navigator, {}, {})
    }
}
