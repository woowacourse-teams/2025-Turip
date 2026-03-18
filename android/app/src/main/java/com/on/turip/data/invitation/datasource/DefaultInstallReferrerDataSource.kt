package com.on.turip.data.invitation.datasource

import android.content.Context
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class DefaultInstallReferrerDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : InstallReferrerDataSource {
    private companion object {
        private const val INSTALL_REFERRER_TIMEOUT_MILLIS: Long = 3_000L
    }

    override suspend fun getInstallReferrer(): Result<String?> =
        runCatching {
            withTimeout(INSTALL_REFERRER_TIMEOUT_MILLIS) {
                suspendCancellableCoroutine<String?> { continuation ->
                    val client = InstallReferrerClient.newBuilder(context).build()

                    continuation.invokeOnCancellation {
                        client.endConnectionSafely()
                    }

                    val listener =
                        createInstallReferrerStateListener(
                            client = client,
                            continuation = continuation,
                        )

                    runCatching {
                        client.startConnection(listener)
                    }.onFailure { exception: Throwable ->
                        client.endConnectionSafely()
                        continuation.resumeWithExceptionSafely(exception)
                    }
                }
            }
        }

    private fun createInstallReferrerStateListener(
        client: InstallReferrerClient,
        continuation: CancellableContinuation<String?>,
    ): InstallReferrerStateListener =
        object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        runCatching {
                            client.installReferrer.installReferrer
                        }.onSuccess { installReferrer: String? ->
                            continuation.resumeSafely(installReferrer)
                        }.onFailure { exception: Throwable ->
                            continuation.resumeWithExceptionSafely(exception)
                        }
                    }

                    // 재시도를 하더라도 처리 할 수 없는 에러
                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED,
                    InstallReferrerClient.InstallReferrerResponse.DEVELOPER_ERROR,
                    InstallReferrerClient.InstallReferrerResponse.PERMISSION_ERROR,
                    -> {
                        continuation.resumeSafely(null)
                    }

                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                        continuation.resumeWithExceptionSafely(
                            IllegalStateException("InstallReferrer SERVICE_UNAVAILABLE 에러 발생"),
                        )
                    }

                    else -> {
                        continuation.resumeWithExceptionSafely(
                            IllegalStateException("InstallReferrer 에러 코드 responseCode:$responseCode"),
                        )
                    }
                }
                client.endConnectionSafely()
            }

            // Google Play 연결이 중단된 경우 처리
            override fun onInstallReferrerServiceDisconnected() {
                continuation.resumeWithExceptionSafely(IllegalStateException("InstallReferrer 연결 중단"))
                client.endConnectionSafely()
            }
        }

    private fun InstallReferrerClient.endConnectionSafely() {
        runCatching { endConnection() }
    }

    private fun <T> CancellableContinuation<T>.resumeSafely(value: T) {
        runCatching { resume(value) }
    }

    private fun <T> CancellableContinuation<T>.resumeWithExceptionSafely(exception: Throwable) {
        runCatching { resumeWithException(exception) }
    }
}
