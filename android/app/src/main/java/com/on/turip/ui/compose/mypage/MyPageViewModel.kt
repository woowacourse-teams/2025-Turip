package com.on.turip.ui.compose.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.bookmark.PagedBookmarkContents
import com.on.turip.domain.bookmark.repository.BookmarkRepository
import com.on.turip.ui.compose.mypage.model.MyPageSectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<MyPageUiState> = MutableStateFlow(MyPageUiState.Idle)
    val uiState: StateFlow<MyPageUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<MyPageUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<MyPageUiEffect> = _uiEffect.receiveAsFlow()

    init {
        loadBookmarkContents()
    }

    fun loadBookmarkContents() {
        viewModelScope.launch {
            _uiState.update { it.copy(bookmarkContentState = MyPageSectionState.Loading) }

            bookmarkRepository
                .loadBookmarks(10, 0L)
                .onSuccess { result: PagedBookmarkContents ->
                    Timber.d("마이페이지 북마크 목록 조회 성공")
                    _uiState.update {
                        it.copy(bookmarkContentState = MyPageSectionState.Success(result.bookmarkContents.toImmutableList()))
                    }
                }.onFailure {
                    Timber.e("마이페이지 북마크 목록 조회 에러 발생")
                    _uiState.update { it.copy(bookmarkContentState = MyPageSectionState.Error) }
                }
        }
    }

    private val removeBookmarkMutex = Mutex()

    // 삭제 진행 중인 콘텐츠에 대해 중복 API 호출 방지용
    private val removingIds = mutableSetOf<Long>()

    // 낙관적 UI
    fun removeBookmark(contentId: Long) {
        viewModelScope.launch {
            val acquired = removeBookmarkMutex.withLock { removingIds.add(contentId) }
            // 이미 삭제 진행 중이라면 반환
            if (!acquired) return@launch

            try {
                val removed =
                    removeBookmarkMutex.withLock {
                        val current = _uiState.value.bookmarkContentState
                        val contents =
                            (current as? MyPageSectionState.Success)?.data ?: return@withLock false

                        // 이미 UI 제거 완료된 상태 (API 호출 완료)
                        if (contents.none { it.content.id == contentId }) return@withLock false

                        val updated =
                            contents.filter { it.content.id != contentId }.toImmutableList()
                        _uiState.update {
                            it.copy(bookmarkContentState = MyPageSectionState.Success(updated))
                        }
                        true
                    }

                if (!removed) return@launch

                bookmarkRepository
                    .deleteBookmark(contentId)
                    .onFailure {
                        _uiEffect.send(MyPageUiEffect.ShowBookmarkRemoveFailed)
                    }
            } finally {
                // 중복 API 호출 방지 리소스 정리
                removeBookmarkMutex.withLock { removingIds.remove(contentId) }
            }
        }
    }
}
