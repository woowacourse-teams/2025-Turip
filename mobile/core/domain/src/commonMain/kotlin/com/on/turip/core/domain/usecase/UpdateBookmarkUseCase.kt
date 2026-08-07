package com.on.turip.core.domain.usecase

import com.on.turip.core.domain.repository.BookmarkRepository
import com.on.turip.core.model.result.TuripResult

class UpdateBookmarkUseCase(
    private val bookmarkRepository: BookmarkRepository,
) {
    suspend operator fun invoke(
        isBookmarked: Boolean,
        contentId: Long,
    ): TuripResult<Unit> =
        if (isBookmarked) {
            bookmarkRepository.createBookmark(contentId)
        } else {
            bookmarkRepository.deleteBookmark(contentId)
        }
}
