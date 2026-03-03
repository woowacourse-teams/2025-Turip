package com.on.turip.ui.compose.login.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.data.login.datasource.GoogleCredentialManager
import com.on.turip.navigation.Navigator
import com.on.turip.ui.compose.home.navigation.HomeNavKey
import com.on.turip.ui.compose.login.LoginScreen

fun EntryProviderScope<NavKey>.loginScreen(
    navigator: Navigator,
    googleCredentialManager: GoogleCredentialManager,
) {
    entry<LoginNavKey> {
        LoginScreen(
            navigateToHome = { navigator.navigate(HomeNavKey) },
            googleCredentialManager = googleCredentialManager,
        )
    }
}
