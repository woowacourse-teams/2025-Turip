package com.on.turip.feature.login.impl.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal expect fun PlatformLoginButton(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
)
