package com.on.turip.feature.trip.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.on.turip.core.domain.repository.BookmarkRepository
import com.on.turip.core.domain.repository.ContentRepository
import com.on.turip.core.ui.BaseViewModel
import kotlinx.coroutines.launch

class TripDetailViewModel(
    private val contentId: Long,
    private val contentRepository: ContentRepository,
    private val bookmarkRepository: BookmarkRepository,
) : BaseViewModel<TripDetailIntent, TripDetailState, TripDetailEffect>(TripDetailState()) {

    init {
        loadContent()
    }

    override fun onIntent(intent: TripDetailIntent) {
        when (intent) {
            TripDetailIntent.Retry -> loadContent()
            TripDetailIntent.ToggleBookmark -> toggleBookmark()
        }
    }

    private fun loadContent() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, isError = false) }
            contentRepository.getContent(contentId)
                .onSuccess { content ->
                    updateState { copy(content = content, isLoading = false) }
                }
                .onFailure {
                    updateState { copy(isLoading = false, isError = true) }
                }
        }
    }

    private fun toggleBookmark() {
        val content = currentState.content ?: return
        val newBookmarked = !content.isBookmarked
        updateState { copy(content = content.copy(isBookmarked = newBookmarked)) }

        viewModelScope.launch {
            val result = if (newBookmarked) {
                bookmarkRepository.addBookmark(contentId)
            } else {
                bookmarkRepository.removeBookmark(contentId)
            }
            result
                .onSuccess { emitEffect(TripDetailEffect.ShowBookmarkStatus(newBookmarked)) }
                .onFailure {
                    updateState { copy(content = content.copy(isBookmarked = !newBookmarked)) }
                    emitEffect(TripDetailEffect.ShowBookmarkError)
                }
        }
    }
}
