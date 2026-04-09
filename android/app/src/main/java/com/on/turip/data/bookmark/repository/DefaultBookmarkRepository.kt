package com.on.turip.data.bookmark.repository

import com.on.turip.core.result.TuripResult
import com.on.turip.core.result.map
import com.on.turip.core.result.mapCatching
import com.on.turip.core.result.onSuccess
import com.on.turip.data.bookmark.datasource.BookmarkRemoteDataSource
import com.on.turip.data.bookmark.toDomain
import com.on.turip.data.bookmark.toRequestDto
import com.on.turip.domain.bookmark.BookmarkContent
import com.on.turip.domain.bookmark.repository.BookmarkRepository
import com.on.turip.domain.common.paging.Cursor
import com.on.turip.domain.common.paging.Page
import com.on.turip.domain.content.repository.ContentRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class DefaultBookmarkRepository @Inject constructor(
    private val bookmarkRemoteDataSource: BookmarkRemoteDataSource,
    private val contentRepository: ContentRepository,
) : BookmarkRepository {
    private val _bookmarkContents =
        MutableStateFlow<ImmutableList<BookmarkContent>>(persistentListOf())
    override val bookmarkContents: StateFlow<ImmutableList<BookmarkContent>> =
        _bookmarkContents.asStateFlow()

    override suspend fun createBookmark(contentId: Long): TuripResult<Unit> =
        bookmarkRemoteDataSource
            .postBookmark(contentId.toRequestDto())
            .also { result ->
                result.onSuccess { response ->
                    val bookmark = response.toDomain()
                    contentRepository.loadTripInfo(contentId).onSuccess { trip ->
                        _bookmarkContents.update { current ->
                            (listOf(BookmarkContent.of(bookmark, trip.tripDuration, trip.tripPlaceCount)) + current).toImmutableList()
                        }
                    }
                }
            }.map { }

    override suspend fun deleteBookmark(contentId: Long): TuripResult<Unit> =
        bookmarkRemoteDataSource.deleteBookmark(contentId).also { result ->
            result.onSuccess {
                _bookmarkContents.update { current ->
                    current.filter { it.content.id != contentId }.toImmutableList()
                }
            }
        }

    override suspend fun loadBookmarks(cursor: Cursor): TuripResult<Page<BookmarkContent>> =
        bookmarkRemoteDataSource.getBookmarks(cursor).mapCatching { it.toDomain() }.also { result ->
            result.onSuccess { page ->
                if (cursor.lastId == null) {
                    _bookmarkContents.value = page.items.toImmutableList()
                } else {
                    _bookmarkContents.update { current ->
                        (current + page.items).toImmutableList()
                    }
                }
            }
        }

    override suspend fun loadBookmarkCount(): TuripResult<Int> = bookmarkRemoteDataSource.getBookmarkCount().mapCatching { it.count }

    override fun clearCache() {
        _bookmarkContents.value = persistentListOf()
    }
}
