package com.on.turip.feature.login.impl

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
