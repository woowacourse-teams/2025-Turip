package com.on.turip.data.login.dto

import androidx.credentials.GetCredentialResponse

sealed interface LoginResponse {
    data class GoogleLogin(
        val getCredentialResponse: GetCredentialResponse,
    ) : LoginResponse
}
