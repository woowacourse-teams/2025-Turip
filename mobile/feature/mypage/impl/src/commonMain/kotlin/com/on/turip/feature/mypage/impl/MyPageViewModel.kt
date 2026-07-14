package com.on.turip.feature.mypage.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.repository.AccountRepository
import com.on.turip.core.domain.repository.BookmarkRepository
import com.on.turip.core.domain.repository.UserStorageRepository
import com.on.turip.core.domain.session.SessionState
import com.on.turip.core.model.account.Account
import com.on.turip.core.model.bookmark.BookmarkContent
import com.on.turip.core.model.paging.Cursor
import com.on.turip.core.model.result.ErrorType
import com.on.turip.core.model.result.onFailure
import com.on.turip.core.model.result.onSuccess
import com.on.turip.core.model.setting.PrivacyPolicy
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.error.UiError
import com.on.turip.core.ui.error.toUiError
import com.on.turip.domain.login.MemberRepository
import com.on.turip.feature.mypage.impl.model.InquiryMail
import com.on.turip.feature.mypage.impl.util.AppEnvironmentInfoProvider
import com.on.turip.feature.mypage.impl.util.toUiModel
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.ImmutableList
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

class MyPageViewModel(
    private val bookmarkRepository: BookmarkRepository,
    private val userStorageRepository: UserStorageRepository,
    private val memberRepository: MemberRepository,
    private val accountRepository: AccountRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {
    private val _uiState: MutableStateFlow<MyPageUiState> = MutableStateFlow(MyPageUiState.Idle)
    val uiState: StateFlow<MyPageUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<MyPageUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<MyPageUiEffect> = _uiEffect.receiveAsFlow()

    val sessionState: StateFlow<SessionState> = sessionManager.state

    init {
        loadProfile(isRetry = false)
        observeBookmarkContents()
        loadBookmarkContents(isRetry = false)
    }

    private fun observeBookmarkContents() {
        viewModelScope.launch {
            bookmarkRepository.bookmarkContents.collect { contents ->
                _uiState.update { state ->
                    val shouldUpdate =
                        when {
                            state.bookmarkContentState is MyPageSectionState.Error -> false
                            contents.isEmpty() && state.bookmarkContentState !is MyPageSectionState.Success -> false
                            else -> true
                        }
                    if (shouldUpdate) {
                        state.copy(
                            bookmarkContentState =
                                MyPageSectionState.Success(
                                    contents.take(MAX_BOOKMARK_DISPLAY_COUNT).toImmutableList(),
                                ),
                        )
                    } else {
                        state
                    }
                }
            }
        }
    }

    fun loadProfile(isRetry: Boolean) {
        viewModelScope.launch {
            accountRepository
                .loadMyProfile()
                .onSuccess { result: Account ->
                    _uiState.update {
                        Napier.d("마이페이지 프로필 조회 성공")
                        it.copy(profileState = MyPageSectionState.Success(result.toUiModel()))
                    }
                }.onFailure {
                    Napier.e("마이페이지 프로필 조회 에러 발생")
                    _uiState.update { it.copy(profileState = MyPageSectionState.Error) }
                    if (isRetry) _uiEffect.send(MyPageUiEffect.ShowProfileLoadFailed)
                }
        }
    }

    fun loadBookmarkContents(isRetry: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(bookmarkContentState = MyPageSectionState.Loading) }

            val cursor = Cursor(size = 10, lastId = null)
            bookmarkRepository
                .loadBookmarks(cursor)
                .onSuccess { page ->
                    _uiState.update { state ->
                        if (state.bookmarkContentState is MyPageSectionState.Loading) {
                            state.copy(
                                bookmarkContentState =
                                    MyPageSectionState.Success(
                                        page.items.take(MAX_BOOKMARK_DISPLAY_COUNT).toImmutableList(),
                                    ),
                            )
                        } else {
                            state
                        }
                    }
                    Napier.d("마이페이지 북마크 목록 조회 성공")
                }.onFailure { errorType ->
                    Napier.e("마이페이지 북마크 목록 조회 에러 발생")

                    if (errorType is ErrorType.Auth) {
                        sessionManager.switchToGuest()
                        _uiEffect.send(MyPageUiEffect.NavigateToLogin)
                        return@onFailure
                    }

                    _uiState.update { it.copy(bookmarkContentState = MyPageSectionState.Error) }
                    if (isRetry) _uiEffect.send(MyPageUiEffect.ShowBookmarksLoadFailed)
                }
        }
    }

    private val removeBookmarkMutex = Mutex()
    private val removingIds = mutableSetOf<Long>()
    private val removingSnapshots = mutableMapOf<Long, BookmarkRemoveSnapshot>()

    fun removeBookmark(contentId: Long) {
        viewModelScope.launch {
            val acquired = removeBookmarkMutex.withLock { removingIds.add(contentId) }
            if (!acquired) return@launch

            try {
                val removed =
                    removeBookmarkMutex.withLock {
                        val current = _uiState.value.bookmarkContentState
                        val contents = (current as? MyPageSectionState.Success)?.data ?: return@withLock false

                        val removeContentIndex: Int = contents.indexOfFirst { it.content.id == contentId }
                        if (removeContentIndex == -1) return@withLock false

                        val removeContent = contents[removeContentIndex]
                        removingSnapshots[contentId] = BookmarkRemoveSnapshot(removeContent)

                        val updated = contents.filter { it.content.id != contentId }.toImmutableList()
                        _uiState.update {
                            it.copy(bookmarkContentState = MyPageSectionState.Success(updated))
                        }
                        true
                    }

                if (!removed) return@launch

                bookmarkRepository
                    .deleteBookmark(contentId)
                    .onSuccess {
                        removeBookmarkMutex.withLock { removingSnapshots.remove(contentId) }
                    }.onFailure {
                        _uiEffect.send(MyPageUiEffect.ShowBookmarkRemoveFailed(contentId))
                    }
            } finally {
                removeBookmarkMutex.withLock { removingIds.remove(contentId) }
            }
        }
    }

    fun rollbackBookmarkContentRemove(contentId: Long) {
        viewModelScope.launch {
            removeBookmarkMutex.withLock {
                val snapshot = removingSnapshots.remove(contentId) ?: return@withLock

                val current = _uiState.value.bookmarkContentState
                val contents = (current as? MyPageSectionState.Success)?.data ?: return@withLock

                if (contents.any { it.content.id == contentId }) return@withLock

                val rollbackContents: ImmutableList<BookmarkContent> =
                    contents
                        .toMutableList()
                        .apply { add(snapshot.content) }
                        .sortedByDescending { it.bookmarkId }
                        .toImmutableList()

                _uiState.update {
                    it.copy(bookmarkContentState = MyPageSectionState.Success(rollbackContents))
                }
            }
        }
    }

    fun loadInquiryMail() {
        viewModelScope.launch {
            val fid =
                userStorageRepository.loadId().getOrNull()?.fid ?: run {
                    Napier.e("문의하기 fid 가져오기 실패")
                    INVALID_FID
                }
            val mail =
                InquiryMail(
                    appEnvironmentInfo = AppEnvironmentInfoProvider.getAppEnvironmentInfo(),
                    fid = fid,
                )

            _uiEffect.send(MyPageUiEffect.NavigateToInquiry(mail))
        }
    }

    fun loadPrivacyPolicy() {
        viewModelScope.launch {
            _uiEffect.send(MyPageUiEffect.NavigateToPrivacyPolicy(PrivacyPolicy.LINK))
        }
    }

    fun loadLogoutDialog() {
        _uiState.update { it.copy(dialogState = MyPageDialogState.LogoutRequired) }
    }

    fun loadWithdrawDialog() {
        _uiState.update { it.copy(dialogState = MyPageDialogState.ConfirmWithdraw) }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(dialogState = null) }
    }

    fun confirmLogout() {
        viewModelScope.launch {
            dismissDialog()
            _uiState.update { it.copy(isLoggingOut = true) }

            memberRepository
                .logout()
                .onSuccess {
                    sessionManager.switchToGuest()
                    _uiEffect.send(MyPageUiEffect.NavigateToLogin)
                    Napier.d("로그아웃 성공")
                }.onFailure { errorType: ErrorType ->
                    handleError(errorType, MyPageRetryAction.LOGOUT)
                    Napier.e("로그아웃 실패")
                }

            _uiState.update { it.copy(isLoggingOut = false) }
        }
    }

    fun confirmWithdraw() {
        viewModelScope.launch {
            dismissDialog()
            _uiState.update { it.copy(isWithdrawing = true) }

            memberRepository
                .deleteMember()
                .onSuccess {
                    sessionManager.switchToGuest()
                    _uiEffect.send(MyPageUiEffect.NavigateToLogin)
                    Napier.d("회원탈퇴 성공")
                }.onFailure { errorType: ErrorType ->
                    handleError(errorType, MyPageRetryAction.WITHDRAW)
                    Napier.e("회원탈퇴 실패")
                }

            _uiState.update { it.copy(isWithdrawing = false) }
        }
    }

    private suspend fun handleError(
        errorType: ErrorType,
        retryAction: MyPageRetryAction,
    ) {
        val uiError: UiError = errorType.toUiError()
        if (uiError is UiError.Global) {
            when (uiError) {
                UiError.Global.Network -> {
                    _uiEffect.send(MyPageUiEffect.ShowError(ErrorUiState.Network, retryAction))
                }

                UiError.Global.Server -> {
                    _uiEffect.send(MyPageUiEffect.ShowError(ErrorUiState.Server, retryAction))
                }

                UiError.Global.TokenExpired -> {
                    sessionManager.switchToGuest()
                    _uiEffect.send(MyPageUiEffect.NavigateToLogin)
                }
            }
        }
    }

    fun handleErrorRetryRequest(action: MyPageRetryAction) {
        when (action) {
            MyPageRetryAction.LOGOUT -> confirmLogout()
            MyPageRetryAction.WITHDRAW -> confirmWithdraw()
        }
    }

    companion object {
        private const val INVALID_FID = "FID_LOAD_FAIL"
        private const val MAX_BOOKMARK_DISPLAY_COUNT = 10
    }
}

private data class BookmarkRemoveSnapshot(
    val content: BookmarkContent,
)
