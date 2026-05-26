package com.on.turip.feature.splash.impl.viewmodel

import androidx.compose.runtime.Immutable
import com.on.turip.core.ui.UiState

@Immutable
data class SplashState(
    val placeholder: Unit = Unit,
) : UiState
