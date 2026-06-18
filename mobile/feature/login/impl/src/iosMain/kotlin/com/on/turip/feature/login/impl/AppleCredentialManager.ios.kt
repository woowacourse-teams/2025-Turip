package com.on.turip.feature.login.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.TuripResult
import io.github.aakira.napier.Napier
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AuthenticationServices.ASPresentationAnchor
import platform.AuthenticationServices.ASAuthorization
import platform.AuthenticationServices.ASAuthorizationAppleIDButton
import platform.AuthenticationServices.ASAuthorizationAppleIDButtonStyle
import platform.AuthenticationServices.ASAuthorizationAppleIDButtonTypeSignIn
import platform.AuthenticationServices.ASAuthorizationAppleIDCredential
import platform.AuthenticationServices.ASAuthorizationAppleIDProvider
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerDelegateProtocol
import platform.AuthenticationServices.ASAuthorizationControllerPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASAuthorizationScopeEmail
import platform.Foundation.NSError
import platform.Foundation.NSUUID
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.darwin.NSObject
import kotlin.coroutines.resume

@Composable
internal actual fun rememberAppleCredentialManager(): AppleCredentialManager =
    remember {
        IosAppleCredentialManager()
    }

private class IosAppleCredentialManager : AppleCredentialManager {
    private var authorizationController: ASAuthorizationController? = null
    private var authorizationDelegate: AppleAuthorizationDelegate? = null

    override suspend fun getCredential(): TuripResult<AppleCredential> =
        suspendCancellableCoroutine { continuation ->
            val rawNonce = generateRawNonce()
            val hashedNonce = sha256(rawNonce)

            val request =
                ASAuthorizationAppleIDProvider()
                    .createRequest()
                    .apply {
                        requestedScopes = listOf(ASAuthorizationScopeEmail)
                        nonce = hashedNonce
                    }
            val controller = ASAuthorizationController(authorizationRequests = listOf(request))
            val delegate =
                AppleAuthorizationDelegate(
                    rawNonce = rawNonce,
                    onCompleted = { result ->
                        authorizationController = null
                        authorizationDelegate = null
                        if (continuation.isActive) {
                            continuation.resume(result)
                        }
                    },
                )

            authorizationController = controller
            authorizationDelegate = delegate
            controller.delegate = delegate
            controller.presentationContextProvider = delegate

            continuation.invokeOnCancellation {
                authorizationController = null
                authorizationDelegate = null
            }

            controller.performRequests()
        }
}

private fun generateRawNonce(): String =
    NSUUID().UUIDString().replace("-", "") + NSUUID().UUIDString().replace("-", "")

private fun sha256(input: String): String =
    sha256Digest(input).joinToString("") { (it.toInt() and 0xff).toString(16).padStart(2, '0') }

private class AppleAuthorizationDelegate(
    private val rawNonce: String,
    private val onCompleted: (TuripResult<AppleCredential>) -> Unit,
) : NSObject(),
    ASAuthorizationControllerDelegateProtocol,
    ASAuthorizationControllerPresentationContextProvidingProtocol {
    @OptIn(BetaInteropApi::class)
    override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithAuthorization: ASAuthorization,
    ) {
        val credential = didCompleteWithAuthorization.credential as? ASAuthorizationAppleIDCredential
        val idToken = credential?.identityToken?.let { data ->
            NSString.create(data = data, encoding = NSUTF8StringEncoding) as String?
        }

        onCompleted(
            if (idToken.isNullOrBlank()) {
                TuripResult.Failure(
                    errorType = ErrorType.Unknown,
                    cause = IllegalStateException("Apple id token is missing."),
                )
            } else {
                TuripResult.Success(AppleCredential(idToken = idToken, rawNonce = rawNonce))
            },
        )
    }

    override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithError: NSError,
    ) {
        val cause = Throwable(
            "Apple authorization failed. domain=${didCompleteWithError.domain}, " +
                "code=${didCompleteWithError.code}, message=${didCompleteWithError.localizedDescription}",
        )
        val errorType =
            if (didCompleteWithError.code == APPLE_AUTHORIZATION_ERROR_CANCELED_CODE) {
                ErrorType.Cancelled
            } else {
                ErrorType.Unknown
            }

        if (errorType == ErrorType.Cancelled) {
            Napier.d(cause.message.orEmpty())
        } else {
            Napier.e("Apple authorization failed", cause)
        }

        onCompleted(
            TuripResult.Failure(
                errorType = errorType,
                cause = cause,
            ),
        )
    }


    @OptIn(ExperimentalForeignApi::class)
    override fun presentationAnchorForAuthorizationController(
        controller: ASAuthorizationController,
    ): ASPresentationAnchor {
        val window = UIApplication.sharedApplication.windows.firstOrNull() as? UIWindow
        return window ?: UIWindow()
    }
}

internal fun createAppleSignInButton(): ASAuthorizationAppleIDButton =
    ASAuthorizationAppleIDButton(
        authorizationButtonType = ASAuthorizationAppleIDButtonTypeSignIn,
        authorizationButtonStyle = ASAuthorizationAppleIDButtonStyle.ASAuthorizationAppleIDButtonStyleBlack,
    )

private const val APPLE_AUTHORIZATION_ERROR_CANCELED_CODE = 1001L
