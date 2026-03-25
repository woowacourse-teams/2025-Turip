package com.on.turip.ui.compose.login.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.data.login.datasource.GoogleCredentialManager
import com.on.turip.navigation.NavKeyProvider
import com.on.turip.navigation.Navigator
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import javax.inject.Inject

class LoginNavKeyProvider @Inject constructor(
    private val googleCredentialManager: GoogleCredentialManager,
) : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(LoginNavKey::class, LoginNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        loginScreen(navigator = navigator, googleCredentialManager = googleCredentialManager)
    }
}
