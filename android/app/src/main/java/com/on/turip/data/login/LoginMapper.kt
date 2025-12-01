package com.on.turip.data.login

import com.on.turip.data.login.dto.LoginJwtTokenResponse
import com.on.turip.data.login.dto.ReissueTokenResponse
import com.on.turip.domain.login.AuthResult
import com.on.turip.domain.login.AuthTokens

fun LoginJwtTokenResponse.toDomain(): AuthResult =
    AuthResult(
        authTokens =
            AuthTokens(
                accessToken = accessToken,
                refreshToken = refreshToken,
            ),
        isNewMember = isNewMember,
    )

fun ReissueTokenResponse.toDomain(): AuthTokens =
    AuthTokens(
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
