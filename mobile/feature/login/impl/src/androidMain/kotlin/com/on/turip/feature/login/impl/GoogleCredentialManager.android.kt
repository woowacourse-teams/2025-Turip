package com.on.turip.feature.login.impl

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.TuripResult
import com.on.turip.feature.login.impl.BuildConfig
import io.github.aakira.napier.Napier
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

@Composable
internal actual fun rememberGoogleCredentialManager(): GoogleCredentialManager {
    val context = LocalContext.current
    return remember(context) {
        AndroidGoogleCredentialManager(
            context = context,
            getCredentialRequest = buildGoogleCredentialRequest(),
        )
    }
}

private class AndroidGoogleCredentialManager(
    private val context: Context,
    private val getCredentialRequest: GetCredentialRequest,
) : GoogleCredentialManager {
    private val credentialManager by lazy { CredentialManager.create(context) }

    override suspend fun getIdToken(): TuripResult<String> =
        try {
            val credentialResponse =
                credentialManager.getCredential(
                    request = getCredentialRequest,
                    context = context,
                )
            TuripResult.Success(handleSignIn(credentialResponse))
        } catch (e: Throwable) {
            if (e is CancellationException) throw e
            Napier.e(
                message = "Google Credential Manager 실패. type=${(e as? GetCredentialException)?.type}, class=${e::class.simpleName}, message=${e.message}",
                throwable = e,
            )
            when (e) {
                is GetCredentialCancellationException -> TuripResult.Failure(ErrorType.Cancelled, e)
                is IOException -> TuripResult.Failure(ErrorType.Network, e)
                else -> TuripResult.Failure(ErrorType.Unknown, e)
            }
        }

    private fun handleSignIn(result: GetCredentialResponse): String {
        val credential: Credential = result.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                return googleIdTokenCredential.idToken
            } catch (e: GoogleIdTokenParsingException) {
                throw IllegalArgumentException("유효하지 않은 ID Token", e)
            }
        }
        throw IllegalArgumentException("Google ID Token Credential 아님. type=${credential::class.simpleName}")
    }
}

private fun buildGoogleCredentialRequest(): GetCredentialRequest {
    val googleOption =
        GetSignInWithGoogleOption
            .Builder(serverClientId = BuildConfig.CLIENT_ID)
            .build()

    return GetCredentialRequest
        .Builder()
        .addCredentialOption(googleOption)
        .build()
}
