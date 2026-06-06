package com.on.turip.core.data.repository

import com.on.turip.core.data.dto.login.LoginJwtTokenResponse
import com.on.turip.core.data.dto.login.ReissueTokenResponse
import com.on.turip.core.model.login.AuthResult
import com.on.turip.core.model.login.AuthTokens

fun LoginJwtTokenResponse.toDomain(): AuthResult =
    AuthResult(
        authTokens = AuthTokens(accessToken = accessToken, refreshToken = refreshToken),
        isMigrationDecided = isMigrationDecided,
    )

fun ReissueTokenResponse.toDomain(): AuthTokens =
    AuthTokens(
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
