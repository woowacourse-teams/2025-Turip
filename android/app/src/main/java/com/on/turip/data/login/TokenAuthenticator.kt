package com.on.turip.data.login

import com.on.turip.data.common.TuripCustomResult
import com.on.turip.domain.login.AuthTokens
import com.on.turip.domain.login.LoginRepository
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val userStorageRepository: UserStorageRepository,
    private val loginRepository: LoginRepository,
) : Authenticator {
    override fun authenticate(
        route: Route?,
        response: Response,
    ): Request? {
        synchronized(this) {
            return runBlocking {
                val accessToken: String? = userStorageRepository.loadAccessToken().getOrNull()
                val refreshToken: String =
                    userStorageRepository.loadRefreshToken().getOrNull() ?: return@runBlocking null

                if (accessToken !=
                    response.request
                        .header("Authorization")
                        ?.removePrefix("Bearer ")
                ) {
                    return@runBlocking response.request
                        .newBuilder()
                        .header("Authorization", "Bearer $accessToken")
                        .build()
                }
                val newTokensResult: TuripCustomResult<AuthTokens> =
                    loginRepository.requestTokens(refreshToken)

                val newTokens: AuthTokens =
                    if (newTokensResult is TuripCustomResult.Success.WithContent) {
                        newTokensResult.data
                    } else {
                        return@runBlocking null
                    }

                userStorageRepository.createTokens(newTokens)

                response.request
                    .newBuilder()
                    .header("Authorization", "Bearer ${newTokens.accessToken}")
                    .build()
            }
        }
    }
}
