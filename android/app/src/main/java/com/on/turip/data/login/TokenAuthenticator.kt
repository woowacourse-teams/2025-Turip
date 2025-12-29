package com.on.turip.data.login

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.domain.login.AuthRepository
import com.on.turip.domain.login.AuthTokens
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val userStorageRepository: UserStorageRepository,
    private val authRepository: AuthRepository,
) : Authenticator {
    override fun authenticate(
        route: Route?,
        response: Response,
    ): Request? {
        synchronized(this) {
            return runBlocking {
                val currentAccessToken: String? =
                    response.request
                        .header("Authorization")
                        ?.removePrefix("Bearer ")

                val storedAccessToken: String? = userStorageRepository.loadAccessToken().getOrNull()
                val storedRefreshToken: String =
                    userStorageRepository.loadRefreshToken().getOrNull() ?: return@runBlocking null

                if (storedAccessToken != currentAccessToken) {
                    return@runBlocking authorizedRequest(response, storedAccessToken)
                }
                val newTokensResult: TuripCustomResult<AuthTokens> =
                    authRepository.requestTokens(storedRefreshToken)

                val newTokens: AuthTokens =
                    if (newTokensResult is TuripCustomResult.Success) {
                        newTokensResult.value
                    } else {
                        return@runBlocking null
                    }

                userStorageRepository.createTokens(newTokens)

                authorizedRequest(response, newTokens.accessToken)
            }
        }
    }

    private fun authorizedRequest(
        response: Response,
        accessToken: String?,
    ): Request =
        response.request
            .newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
}
