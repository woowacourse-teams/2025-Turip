package com.on.turip.ui.compose.login

sealed interface LoginUiEvent {
    data object NavigateToMain : LoginUiEvent
}
