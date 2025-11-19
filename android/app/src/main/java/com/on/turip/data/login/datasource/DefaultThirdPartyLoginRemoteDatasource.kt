package com.on.turip.data.login.datasource

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.on.turip.data.common.TuripCustomResult
import com.on.turip.data.login.dto.LoginResponse
import javax.inject.Inject

class DefaultThirdPartyLoginRemoteDatasource @Inject constructor(
    private val context: Context,
    private val getCredentialRequest: GetCredentialRequest,
) : ThirdPartyLoginRemoteDatasource {
    private val credentialManager = CredentialManager.create(context)

    override suspend fun getIdToken(): TuripCustomResult<LoginResponse> =
        runCatching {
            credentialManager.getCredential(
                request = getCredentialRequest,
                context = context,
            )
        }.fold(
            onSuccess = { result ->
                TuripCustomResult.success(LoginResponse.GoogleLogin(result))
            },
            onFailure = { error ->
                TuripCustomResult.NetworkError(error)
            },
        )
}
