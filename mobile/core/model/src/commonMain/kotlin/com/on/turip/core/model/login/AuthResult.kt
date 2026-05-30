package com.on.turip.core.model.login

data class AuthResult(
    val authTokens: AuthTokens,
    val isMigrationDecided: Boolean,
)
