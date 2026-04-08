package com.on.turip.ui.compose.login

data class LoginUiState(
    val showHelpText: Boolean,
    val showMigrationDialog: Boolean,
    val deepLinkUrl: String?,
) {
    companion object {
        val IDLE: LoginUiState =
            LoginUiState(
                showHelpText = false,
                showMigrationDialog = false,
                deepLinkUrl = null,
            )
    }
}
