package com.on.turip.domain.login

data class AuthResult(
    val authTokens: AuthTokens,
    val isMigrationDecided: Boolean,
)
