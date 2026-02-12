package com.on.turip.ui.compose.login

data class LoginUiState(
    val showHelpText: Boolean,
    val showMigrationDialog: Boolean,
) {
    companion object {
        val IDLE: LoginUiState = LoginUiState(showHelpText = false, showMigrationDialog = false)
    }
}
