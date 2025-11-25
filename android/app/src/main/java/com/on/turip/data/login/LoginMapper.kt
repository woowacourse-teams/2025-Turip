package com.on.turip.data.login

import com.on.turip.data.login.dto.LoginJwtTokenResponse
import com.on.turip.domain.login.AuthTokens

fun LoginJwtTokenResponse.toDomain(): AuthTokens =
    AuthTokens(
        accessToken = accessToken,
        refreshToken = refreshToken,
        isNewMember = isNewMember,
    )
