package com.on.turip.ui.compose.login.model

sealed interface LoginUiEvent {
    data object NavigateToMain : LoginUiEvent
}
