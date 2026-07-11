package com.on.turip.feature.login.impl

data class LoginUiState(
    val showHelpText: Boolean,
    val showMigrationDialog: Boolean,
    val deepLinkUrl: String?,
    val isLoading: Boolean,
) {
    companion object {
        val IDLE: LoginUiState =
            LoginUiState(
                showHelpText = false,
                showMigrationDialog = false,
                deepLinkUrl = null,
                isLoading = false,
            )
    }
}
