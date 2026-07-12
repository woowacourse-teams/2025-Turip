package com.on.turip.feature.login.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.TuripResult
import io.github.aakira.napier.Napier
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.cValue
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AuthenticationServices.ASAuthorization
import platform.AuthenticationServices.ASAuthorizationAppleIDCredential
import platform.AuthenticationServices.ASAuthorizationAppleIDProvider
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerDelegateProtocol
import platform.AuthenticationServices.ASAuthorizationControllerPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASAuthorizationErrorCanceled
import platform.AuthenticationServices.ASAuthorizationErrorDomain
import platform.AuthenticationServices.ASAuthorizationScopeEmail
import platform.AuthenticationServices.ASPresentationAnchor
import platform.Foundation.NSError
import platform.Foundation.NSOperatingSystemVersion
import platform.Foundation.NSProcessInfo
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Security.SecRandomCopyBytes
import platform.Security.kSecRandomDefault
import platform.UIKit.UIApplication
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import platform.darwin.NSObject
import kotlin.coroutines.resume

@Composable
internal actual fun rememberAppleCredentialManager(): AppleCredentialManager =
    remember {
        IosAppleCredentialManager()
    }

private class IosAppleCredentialManager : AppleCredentialManager {
    private var activeRequestId: Any? = null
    private var authorizationController: ASAuthorizationController? = null
    private var authorizationDelegate: AppleAuthorizationDelegate? = null

    override suspend fun getCredential(): TuripResult<AppleCredential> {
        if (activeRequestId != null) {
            return TuripResult.Failure(
                errorType = ErrorType.Cancelled,
                cause = IllegalStateException("Apple 로그인 요청이 이미 진행 중입니다."),
            )
        }

        return suspendCancellableCoroutine { continuation ->
            val requestId = Any()
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
                        if (activeRequestId === requestId) {
                            activeRequestId = null
                            authorizationController = null
                            authorizationDelegate = null
                        }
                        if (continuation.isActive) {
                            continuation.resume(result)
                        }
                    },
                )

            activeRequestId = requestId
            authorizationController = controller
            authorizationDelegate = delegate
            controller.delegate = delegate
            controller.presentationContextProvider = delegate

            continuation.invokeOnCancellation {
                if (activeRequestId === requestId) {
                    activeRequestId = null
                    authorizationController = null
                    authorizationDelegate = null
                }
                cancelIfSupported(controller)
            }

            controller.performRequests()
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun cancelIfSupported(controller: ASAuthorizationController) {
    val minimumVersion =
        cValue<NSOperatingSystemVersion> {
            majorVersion = 16
            minorVersion = 0
            patchVersion = 0
        }
    if (NSProcessInfo.processInfo.isOperatingSystemAtLeastVersion(minimumVersion)) {
        controller.cancel()
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun generateRawNonce(): String {
    val nonceBytes = ByteArray(NONCE_BYTE_LENGTH)
    nonceBytes.usePinned { pinned ->
        SecRandomCopyBytes(kSecRandomDefault, NONCE_BYTE_LENGTH.convert(), pinned.addressOf(0))
    }
    return nonceBytes.joinToString("") { (it.toInt() and 0xff).toString(16).padStart(2, '0') }
}

private const val NONCE_BYTE_LENGTH = 32

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
            if (didCompleteWithError.domain == ASAuthorizationErrorDomain &&
                didCompleteWithError.code == ASAuthorizationErrorCanceled
            ) {
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
        val activeSceneKeyWindow = UIApplication.sharedApplication.connectedScenes
            .filterIsInstance<UIWindowScene>()
            .firstOrNull { it.activationState == UISceneActivationStateForegroundActive }
            ?.windows
            ?.filterIsInstance<UIWindow>()
            ?.firstOrNull { it.keyWindow }

        val fallbackWindow = UIApplication.sharedApplication.windows
            .filterIsInstance<UIWindow>()
            .firstOrNull { it.keyWindow }
            ?: UIApplication.sharedApplication.windows.firstOrNull() as? UIWindow

        return activeSceneKeyWindow ?: checkNotNull(fallbackWindow) {
            "No UIWindow available to present Apple authorization sheet."
        }
    }
}
