package com.on.turip.core.local.datasourceimpl

import android.content.Context
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.on.turip.core.data.datasource.InstallReferrerDataSource
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class DefaultInstallReferrerDataSource(
    private val context: Context,
) : InstallReferrerDataSource {
    private companion object {
        private const val INSTALL_REFERRER_TIMEOUT_MILLIS: Long = 3_000L
    }

    override suspend fun getInstallReferrer(): Result<String?> =
        runCatching {
            withTimeout(INSTALL_REFERRER_TIMEOUT_MILLIS) {
                suspendCancellableCoroutine { continuation ->
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
                    }.onFailure { exception ->
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
                        }.onSuccess { installReferrer ->
                            continuation.resumeSafely(installReferrer)
                        }.onFailure { exception ->
                            continuation.resumeWithExceptionSafely(exception)
                        }
                    }

                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED,
                    InstallReferrerClient.InstallReferrerResponse.DEVELOPER_ERROR,
                    InstallReferrerClient.InstallReferrerResponse.PERMISSION_ERROR,
                    -> {
                        continuation.resumeSafely(null)
                    }

                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                        continuation.resumeWithExceptionSafely(
                            IllegalStateException("InstallReferrer SERVICE_UNAVAILABLE error"),
                        )
                    }

                    else -> {
                        continuation.resumeWithExceptionSafely(
                            IllegalStateException("InstallReferrer error responseCode=$responseCode"),
                        )
                    }
                }
                client.endConnectionSafely()
            }

            override fun onInstallReferrerServiceDisconnected() {
                continuation.resumeWithExceptionSafely(IllegalStateException("InstallReferrer disconnected"))
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
