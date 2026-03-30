package com.on.turip.ui.compose.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.account.Account
import com.on.turip.domain.account.AccountRepository
import com.on.turip.domain.bookmark.BookmarkContent
import com.on.turip.domain.bookmark.repository.BookmarkRepository
import com.on.turip.domain.common.paging.Cursor
import com.on.turip.domain.login.MemberRepository
import com.on.turip.domain.session.SessionState
import com.on.turip.domain.session.SessionStore
import com.on.turip.domain.session.usecase.SwitchToGuestUseCase
import com.on.turip.domain.setting.PrivacyPolicy
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
import com.on.turip.ui.compose.mypage.model.InquiryMail
import com.on.turip.ui.compose.mypage.util.AppEnvironmentInfoProvider
import com.on.turip.ui.compose.mypage.util.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
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
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
    private val userStorageRepository: UserStorageRepository,
    private val memberRepository: MemberRepository,
    private val accountRepository: AccountRepository,
    private val switchToGuestUseCase: SwitchToGuestUseCase,
    sessionStore: SessionStore,
) : ViewModel() {
    private val _uiState: MutableStateFlow<MyPageUiState> = MutableStateFlow(MyPageUiState.Idle)
    val uiState: StateFlow<MyPageUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<MyPageUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<MyPageUiEffect> = _uiEffect.receiveAsFlow()

    val sessionState: StateFlow<SessionState> = sessionStore.state

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
                        state.copy(bookmarkContentState = MyPageSectionState.Success(contents))
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
                        Timber.d("마이페이지 프로필 조회 성공")
                        it.copy(profileState = MyPageSectionState.Success(result.toUiModel()))
                    }
                }.onFailure {
                    Timber.e("마이페이지 프로필 조회 에러 발생")
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
                .onSuccess {
                    _uiState.update { state ->
                        if (state.bookmarkContentState is MyPageSectionState.Loading) {
                            state.copy(bookmarkContentState = MyPageSectionState.Success(emptyList<BookmarkContent>().toImmutableList()))
                        } else {
                            state
                        }
                    }
                    Timber.d("마이페이지 북마크 목록 조회 성공")
                }.onFailure {
                    Timber.e("마이페이지 북마크 목록 조회 에러 발생")
                    _uiState.update { it.copy(bookmarkContentState = MyPageSectionState.Error) }
                    if (isRetry) _uiEffect.send(MyPageUiEffect.ShowBookmarksLoadFailed)
                }
        }
    }

    private val removeBookmarkMutex = Mutex()

    // 삭제 진행 중인 콘텐츠에 대해 중복 API 호출 방지용
    private val removingIds = mutableSetOf<Long>()

    private val removingSnapshots = mutableMapOf<Long, BookmarkRemoveSnapshot>()

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

                        val removeContentIndex: Int =
                            contents.indexOfFirst { it.content.id == contentId }
                        if (removeContentIndex == -1) return@withLock false

                        val removeContent = contents[removeContentIndex]
                        removingSnapshots[contentId] = BookmarkRemoveSnapshot(removeContent)

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
                    .onSuccess {
                        removeBookmarkMutex.withLock { removingSnapshots.remove(contentId) }
                    }.onFailure {
                        _uiEffect.send(MyPageUiEffect.ShowBookmarkRemoveFailed(contentId))
                    }
            } finally {
                // 중복 API 호출 방지 리소스 정리
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

                // 중복 복구 방지
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
                    Timber.e("문의하기 fid 가져오기 실패")
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

            memberRepository
                .logout()
                .onSuccess {
                    switchToGuestUseCase()
                    _uiEffect.send(MyPageUiEffect.NavigateToLogin)
                    Timber.d("로그아웃 성공")
                }.onFailure { errorType: ErrorType ->
                    handleError(errorType, MyPageRetryAction.LOGOUT)
                    Timber.e("로그아웃 실패")
                }
        }
    }

    fun confirmWithdraw() {
        viewModelScope.launch {
            dismissDialog()

            memberRepository
                .deleteMember()
                .onSuccess {
                    switchToGuestUseCase()
                    _uiEffect.send(MyPageUiEffect.NavigateToLogin)
                    Timber.d("회원탈퇴 성공")
                }.onFailure { errorType: ErrorType ->
                    handleError(errorType, MyPageRetryAction.WITHDRAW)
                    Timber.e("회원탈퇴 실패")
                }
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
    }
}

private data class BookmarkRemoveSnapshot(
    val content: BookmarkContent,
)
