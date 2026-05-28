package com.on.turip.feature.login.impl.viewmodel

import androidx.compose.runtime.Immutable
import com.on.turip.core.ui.UiState

@Immutable
data class LoginState(
    val isLoading: Boolean = false,
) : UiState
