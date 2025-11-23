package com.on.turip.data.login.repository

import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.on.turip.data.common.onSuccess
import com.on.turip.data.login.datasource.ThirdPartyLoginRemoteDatasource
import com.on.turip.data.login.dto.LoginResponse
import com.on.turip.domain.login.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class DefaultLoginRepository @Inject constructor(
    val thirdPartyLoginRemoteDatasource: ThirdPartyLoginRemoteDatasource,
) : LoginRepository {
    override fun login() {
        CoroutineScope(Dispatchers.IO).launch {
            val idTokenResponse =
                async {
                    thirdPartyLoginRemoteDatasource.getIdToken()
                }.await()

            idTokenResponse.onSuccess { result: LoginResponse ->
                when (result) {
                    is LoginResponse.GoogleLogin -> handleSignIn(result.getCredentialResponse)
                }
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        when (val credential: Credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        Timber.d("인증 성공, id token = ${googleIdTokenCredential.idToken}")
                    } catch (e: GoogleIdTokenParsingException) {
                        Timber.e("유효하지 않은 id token : $e")
                    }
                } else {
                    Timber.e("구글 ID TOKEN 타입이 아닌 CustomCredential , credential type = ${credential.type}")
                }
            }
        }
    }
}
