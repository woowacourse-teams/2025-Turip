package com.on.turip.data.login.datasource

import android.content.Context
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.TuripResult
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import okio.IOException

class GoogleCredentialManager @Inject constructor(
    @ActivityContext private val context: Context,
    private val getCredentialRequest: GetCredentialRequest,
) : CredentialProvider {
    private val credentialManager by lazy { CredentialManager.create(context) }

    override suspend fun getIdToken(): TuripResult<GoogleIdTokenCredential> =
        try {
            val googleIdTokenCredential =
                credentialManager.getCredential(request = getCredentialRequest, context = context)
            TuripResult.Success(handleSignIn(googleIdTokenCredential))
        } catch (e: Throwable) {
            if (e is CancellationException) throw e
            when {
                e is IOException -> TuripResult.Failure(ErrorType.Network, e)
                else -> TuripResult.Failure(ErrorType.Unknown, e)
            }
        }

    private fun handleSignIn(result: GetCredentialResponse): GoogleIdTokenCredential {
        val credential: Credential = result.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                return GoogleIdTokenCredential.createFrom(credential.data)
            } catch (e: GoogleIdTokenParsingException) {
                throw IllegalArgumentException("유효하지 않은 ID Token", e)
            }
        }
        throw IllegalArgumentException("Google ID Token Credential 아님. type=${credential::class.simpleName}")
    }
}
