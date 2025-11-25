package com.on.turip.ui.compose.login

data class LoginUiState(
    val showHelpText: Boolean,
) {
    companion object {
        val EMPTY: LoginUiState = LoginUiState(showHelpText = false)
    }
}
