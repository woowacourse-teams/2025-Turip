package com.on.turip.domain.session.usecase

import com.on.turip.domain.bookmark.repository.BookmarkRepository
import com.on.turip.domain.session.SessionStore
import com.on.turip.domain.session.TokenManager
import com.on.turip.domain.turip.repository.TuripRepository
import timber.log.Timber
import javax.inject.Inject

class SwitchToGuestUseCase @Inject constructor(
    private val tokenManager: TokenManager,
    private val sessionStore: SessionStore,
    private val bookmarkRepository: BookmarkRepository,
    private val turipRepository: TuripRepository,
) {
    suspend operator fun invoke(): Result<Unit> {
        bookmarkRepository.clearCache()
        turipRepository.clearCache()
        val clearResult: Result<Unit> = tokenManager.clearTokens()
        sessionStore.setGuest()

        return clearResult.onFailure {
            Timber.e("게스트 전환 중 로컬 토큰 삭제 실패")
        }
    }
}
