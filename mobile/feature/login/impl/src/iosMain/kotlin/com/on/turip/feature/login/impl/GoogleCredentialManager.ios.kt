package com.on.turip.feature.login.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.TuripResult
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Parameters
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import platform.AuthenticationServices.ASPresentationAnchor
import platform.AuthenticationServices.ASWebAuthenticationPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASWebAuthenticationSession
import platform.Foundation.NSCharacterSet
import platform.Foundation.NSArray
import platform.Foundation.NSBundle
import platform.Foundation.NSDictionary
import platform.Foundation.NSError
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.Foundation.URLQueryAllowedCharacterSet
import platform.Foundation.stringByAddingPercentEncodingWithAllowedCharacters
import platform.Foundation.stringByRemovingPercentEncoding
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.darwin.NSObject
import kotlin.coroutines.resume

@Composable
internal actual fun rememberGoogleCredentialManager(): GoogleCredentialManager =
    remember {
        IosGoogleCredentialManager()
    }

private class IosGoogleCredentialManager :
    GoogleCredentialManager {
    private var authenticationSession: ASWebAuthenticationSession? = null
    private val presentationContextProvider = GooglePresentationContextProvider()

    override suspend fun getIdToken(): TuripResult<String> {
        val clientId = BuildKonfig.IOS_CLIENT_ID.takeIf { it.isNotBlank() }
            ?: return TuripResult.Failure(
                errorType = ErrorType.Unknown,
                cause = IllegalStateException("Google iOS client id is empty. Set ios_client_id in local.properties."),
            )

        val callbackScheme = clientId.toGoogleReverseClientId()
        if (!isUrlSchemeRegistered(callbackScheme)) {
            return TuripResult.Failure(
                errorType = ErrorType.Unknown,
                cause = IllegalStateException("Google callback URL scheme is not registered: $callbackScheme"),
            )
        }

        val state = NSUUID().UUIDString()
        val nonce = NSUUID().UUIDString()
        val codeVerifier = createPkceCodeVerifier()
        val redirectUri = "$callbackScheme:/oauth2redirect"
        val authorizationUrl =
            buildGoogleAuthorizationUrl(
                clientId = clientId,
                redirectUri = redirectUri,
                state = state,
                nonce = nonce,
                codeChallenge = sha256Base64Url(codeVerifier),
            )

        val authorizationCode =
            when (
                val codeResult = getAuthorizationCode(
                    authorizationUrl = authorizationUrl,
                    callbackScheme = callbackScheme,
                    expectedState = state,
                )
            ) {
                is TuripResult.Failure -> return codeResult
                is TuripResult.Success -> codeResult.value
            }

        return exchangeAuthorizationCodeForIdToken(
            clientId = clientId,
            redirectUri = redirectUri,
            code = authorizationCode,
            codeVerifier = codeVerifier,
        )
    }

    private suspend fun getAuthorizationCode(
        authorizationUrl: String,
        callbackScheme: String,
        expectedState: String,
    ): TuripResult<String> =
        suspendCancellableCoroutine { continuation ->
            val session =
                ASWebAuthenticationSession(
                    uRL = NSURL.URLWithString(authorizationUrl) ?: run {
                        continuation.resume(
                            TuripResult.Failure(
                                errorType = ErrorType.Unknown,
                                cause = IllegalArgumentException("Invalid Google authorization URL"),
                            ),
                        )
                        return@suspendCancellableCoroutine
                    },
                    callbackURLScheme = callbackScheme,
                ) { callbackUrl: NSURL?, error: NSError? ->
                    authenticationSession = null

                    val result =
                        when {
                            error != null -> {
                                Napier.e(
                                    message = "iOS Google login failed. code=${error.code}, domain=${error.domain}, message=${error.localizedDescription}",
                                )
                                TuripResult.Failure(
                                    errorType = if (error.code == AS_WEB_AUTHENTICATION_SESSION_CANCELED_LOGIN) {
                                        ErrorType.Cancelled
                                    } else {
                                        ErrorType.Unknown
                                    },
                                    cause = Throwable(error.localizedDescription),
                                )
                            }

                            callbackUrl == null -> {
                                TuripResult.Failure(
                                    errorType = ErrorType.Unknown,
                                    cause = IllegalStateException("Google login callback URL is null"),
                                )
                            }

                            else -> {
                                parseGoogleAuthorizationCode(callbackUrl, expectedState = expectedState)
                            }
                        }

                    if (continuation.isActive) {
                        continuation.resume(result)
                    }
                }

            session.presentationContextProvider = presentationContextProvider
            session.prefersEphemeralWebBrowserSession = true
            authenticationSession = session

            continuation.invokeOnCancellation {
                authenticationSession?.cancel()
                authenticationSession = null
            }

            if (!session.start() && continuation.isActive) {
                authenticationSession = null
                continuation.resume(
                    TuripResult.Failure(
                        errorType = ErrorType.Unknown,
                        cause = IllegalStateException("Failed to start Google authentication session"),
                    ),
                )
            }
        }

}

private class GooglePresentationContextProvider :
    NSObject(),
    ASWebAuthenticationPresentationContextProvidingProtocol {
    @OptIn(ExperimentalForeignApi::class)
    override fun presentationAnchorForWebAuthenticationSession(
        session: ASWebAuthenticationSession,
    ): ASPresentationAnchor {
        val window =
            UIApplication.sharedApplication.windows.firstOrNull() as? UIWindow
        return window ?: UIWindow()
    }
}

private fun buildGoogleAuthorizationUrl(
    clientId: String,
    redirectUri: String,
    state: String,
    nonce: String,
    codeChallenge: String,
): String =
    "https://accounts.google.com/o/oauth2/v2/auth" +
        "?client_id=${clientId.urlEncode()}" +
        "&redirect_uri=${redirectUri.urlEncode()}" +
        "&response_type=code" +
        "&scope=${"openid email profile".urlEncode()}" +
        "&state=${state.urlEncode()}" +
        "&nonce=${nonce.urlEncode()}" +
        "&code_challenge=${codeChallenge.urlEncode()}" +
        "&code_challenge_method=S256" +
        "&prompt=select_account"

private suspend fun exchangeAuthorizationCodeForIdToken(
    clientId: String,
    redirectUri: String,
    code: String,
    codeVerifier: String,
): TuripResult<String> {
    val httpClient = HttpClient(Darwin)
    return try {
        val response =
            httpClient.post(GOOGLE_TOKEN_ENDPOINT) {
                setBody(
                    FormDataContent(
                        Parameters.build {
                            append("client_id", clientId)
                            append("redirect_uri", redirectUri)
                            append("grant_type", "authorization_code")
                            append("code", code)
                            append("code_verifier", codeVerifier)
                        },
                    ),
                )
            }
        val body = response.body<String>()
        if (response.status.value !in 200..299) {
            return TuripResult.Failure(
                errorType = ErrorType.Unknown,
                cause = IllegalStateException("Google token exchange failed. status=${response.status.value}, body=$body"),
            )
        }

        parseGoogleTokenResponse(body)
    } catch (throwable: Throwable) {
        TuripResult.Failure(
            errorType = ErrorType.Unknown,
            cause = throwable,
        )
    } finally {
        httpClient.close()
    }
}

private fun parseGoogleTokenResponse(body: String): TuripResult<String> {
    val json = Json.parseToJsonElement(body).jsonObject
    val error = json["error"]?.jsonPrimitive?.contentOrNull
    if (!error.isNullOrBlank()) {
        return TuripResult.Failure(
            errorType = ErrorType.Unknown,
            cause = IllegalStateException("Google token exchange failed. error=$error"),
        )
    }

    val idToken = json["id_token"]?.jsonPrimitive?.contentOrNull
    return if (idToken.isNullOrBlank()) {
        TuripResult.Failure(
            errorType = ErrorType.Unknown,
            cause = IllegalStateException("Google id token is missing from token response"),
        )
    } else {
        TuripResult.Success(idToken)
    }
}

private fun parseGoogleAuthorizationCode(
    callbackUrl: NSURL,
    expectedState: String,
): TuripResult<String> {
    val callback = callbackUrl.absoluteString ?: ""
    val parameters = callback.extractOAuthParameters()
    val error = parameters["error"]
    if (!error.isNullOrBlank()) {
        return TuripResult.Failure(
            errorType = if (error == "access_denied") ErrorType.Cancelled else ErrorType.Unknown,
            cause = IllegalStateException("Google login failed. error=$error"),
        )
    }

    if (parameters["state"] != expectedState) {
        return TuripResult.Failure(
            errorType = ErrorType.Unknown,
            cause = IllegalStateException("Google login state mismatch"),
        )
    }

    val code = parameters["code"]?.urlDecode()
    return if (code.isNullOrBlank()) {
        TuripResult.Failure(
            errorType = ErrorType.Unknown,
            cause = IllegalStateException("Google authorization code is missing"),
        )
    } else {
        TuripResult.Success(code)
    }
}

private fun String.extractOAuthParameters(): Map<String, String> {
    val query = substringAfter('?', missingDelimiterValue = "")
        .substringBefore('#')
    val fragment = substringAfter('#', missingDelimiterValue = "")
    return query.toOAuthParameterMap() + fragment.toOAuthParameterMap()
}

private fun String.toOAuthParameterMap(): Map<String, String> =
    split('&')
        .mapNotNull { rawItem ->
            if (rawItem.isBlank()) return@mapNotNull null
            val separatorIndex = rawItem.indexOf('=')
            if (separatorIndex < 0) return@mapNotNull rawItem to ""
            rawItem.substring(0, separatorIndex) to rawItem.substring(separatorIndex + 1)
        }.toMap()

private fun String.toGoogleReverseClientId(): String =
    "com.googleusercontent.apps.${removeSuffix(".apps.googleusercontent.com")}"

private fun String.urlEncode(): String =
    (this as NSStringBackedString)
        .stringByAddingPercentEncodingWithAllowedCharacters(NSCharacterSet.URLQueryAllowedCharacterSet)
        ?: this

private fun String.urlDecode(): String =
    (this as NSStringBackedString).stringByRemovingPercentEncoding ?: this

private fun createPkceCodeVerifier(): String =
    "${NSUUID().UUIDString()}-${NSUUID().UUIDString()}"

private fun sha256Base64Url(input: String): String = base64UrlEncode(sha256Digest(input))

private fun base64UrlEncode(bytes: ByteArray): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_"
    val sb = StringBuilder()
    var i = 0
    while (i < bytes.size) {
        val b0 = bytes[i].toInt() and 0xFF
        val b1 = if (i + 1 < bytes.size) bytes[i + 1].toInt() and 0xFF else 0
        val b2 = if (i + 2 < bytes.size) bytes[i + 2].toInt() and 0xFF else 0
        sb.append(chars[b0 shr 2])
        sb.append(chars[((b0 and 0x3) shl 4) or (b1 shr 4)])
        if (i + 1 < bytes.size) sb.append(chars[((b1 and 0xF) shl 2) or (b2 shr 6)])
        if (i + 2 < bytes.size) sb.append(chars[b2 and 0x3F])
        i += 3
    }
    return sb.toString()
}

private fun isUrlSchemeRegistered(scheme: String): Boolean {
    val urlTypes = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleURLTypes") as? NSArray
        ?: return false

    for (index in 0UL until urlTypes.count) {
        val urlType = urlTypes.objectAtIndex(index) as? NSDictionary ?: continue
        val schemes = urlType.objectForKey("CFBundleURLSchemes") as? NSArray ?: continue
        for (schemeIndex in 0UL until schemes.count) {
            if (schemes.objectAtIndex(schemeIndex) == scheme) return true
        }
    }
    return false
}

private typealias NSStringBackedString = platform.Foundation.NSString

private const val AS_WEB_AUTHENTICATION_SESSION_CANCELED_LOGIN = 1L
private const val GOOGLE_TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token"
