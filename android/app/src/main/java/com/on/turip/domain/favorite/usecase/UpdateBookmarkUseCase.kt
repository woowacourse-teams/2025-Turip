package com.on.turip.domain.favorite.usecase

import com.on.turip.core.result.TuripResult
import com.on.turip.domain.favorite.repository.BookmarkRepository
import javax.inject.Inject

class UpdateBookmarkUseCase @Inject constructor(
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
