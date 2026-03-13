package com.on.turip.data.invitation.repository

import com.on.turip.data.invitation.datasource.DeferredDeepLinkLocalDataSource
import com.on.turip.data.invitation.datasource.InstallReferrerDataSource
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DefaultDeferredDeepLinkRepositoryTest {
    @Test
    fun `install referrer token 추출에 성공하면 handled 처리한다`() =
        runTest {
            val localDataSource = FakeDeferredDeepLinkLocalDataSource()
            val repository =
                DefaultDeferredDeepLinkRepository(
                    installReferrerDataSource =
                        FakeInstallReferrerDataSource(result = Result.success("token=abc123")),
                    deferredDeepLinkLocalDataSource = localDataSource,
                )

            val actual = repository.resolveDeferredInvitationToken().getOrNull()

            assertThat(actual?.value).isEqualTo("abc123")
            assertThat(localDataSource.handled).isTrue()
        }

    @Test
    fun `install referrer 조회 실패면 이번 실행은 null을 반환하고 다음 실행에 재시도한다`() =
        runTest {
            val localDataSource = FakeDeferredDeepLinkLocalDataSource()
            val repository =
                DefaultDeferredDeepLinkRepository(
                    installReferrerDataSource =
                        FakeInstallReferrerDataSource(
                            result = Result.failure(IllegalStateException("fail")),
                        ),
                    deferredDeepLinkLocalDataSource = localDataSource,
                )

            val actual = repository.resolveDeferredInvitationToken().getOrNull()

            assertThat(actual).isNull()
            assertThat(localDataSource.handled).isFalse()
        }

    @Test
    fun `이미 handled면 install referrer 조회를 건너뛴다`() =
        runTest {
            val localDataSource =
                FakeDeferredDeepLinkLocalDataSource(
                    handled = true,
                )
            val installReferrerDataSource =
                FakeInstallReferrerDataSource(result = Result.success("token=abc123"))
            val repository =
                DefaultDeferredDeepLinkRepository(
                    installReferrerDataSource = installReferrerDataSource,
                    deferredDeepLinkLocalDataSource = localDataSource,
                )

            val actual = repository.resolveDeferredInvitationToken().getOrNull()

            assertThat(actual).isNull()
            assertThat(localDataSource.handled).isTrue()
            assertThat(installReferrerDataSource.callCount).isZero()
        }

    private class FakeInstallReferrerDataSource(
        private val result: Result<String?>,
    ) : InstallReferrerDataSource {
        var callCount: Int = 0

        override suspend fun getInstallReferrer(): Result<String?> {
            callCount += 1
            return result
        }
    }

    private class FakeDeferredDeepLinkLocalDataSource(
        var handled: Boolean = false,
    ) : DeferredDeepLinkLocalDataSource {
        override suspend fun isInstallReferrerHandled(): Boolean = handled

        override suspend fun markInstallReferrerHandled() {
            handled = true
        }
    }
}
