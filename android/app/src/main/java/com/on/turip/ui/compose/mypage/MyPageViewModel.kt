package com.on.turip.ui.compose.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.turip.core.result.ErrorType
import com.on.turip.core.result.onFailure
import com.on.turip.core.result.onSuccess
import com.on.turip.domain.accounts.Account
import com.on.turip.domain.accounts.AccountRepository
import com.on.turip.domain.bookmark.PagedBookmarkContents
import com.on.turip.domain.bookmark.repository.BookmarkRepository
import com.on.turip.domain.login.MemberRepository
import com.on.turip.domain.setting.PrivacyPolicy
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.UiError
import com.on.turip.ui.common.error.toUiError
import com.on.turip.ui.compose.mypage.model.MyPageSectionState
import com.on.turip.ui.compose.setting.model.InquiryMail
import com.on.turip.ui.compose.setting.util.AppEnvironmentInfoProvider
import dagger.hilt.android.lifecycle.HiltViewModel
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
) : ViewModel() {
    private val _uiState: MutableStateFlow<MyPageUiState> = MutableStateFlow(MyPageUiState.Idle)
    val uiState: StateFlow<MyPageUiState> = _uiState.asStateFlow()

    private val _uiEffect: Channel<MyPageUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect: Flow<MyPageUiEffect> = _uiEffect.receiveAsFlow()

    init {
        loadProfile()
        loadBookmarkContents()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(profileState = MyPageSectionState.Loading) }

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
                }
        }
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
                    userStorageRepository
                        .clearTokens()
                        .onSuccess {
                            _uiEffect.send(MyPageUiEffect.NavigateToLogin)
                            Timber.d("로그아웃 성공")
                        }.onFailure {
                            Timber.e("토큰 초기화 실패")
                        }
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
                    userStorageRepository
                        .clearTokens()
                        .onSuccess {
                            _uiEffect.send(MyPageUiEffect.NavigateToLogin)
                            Timber.d("회원탈퇴 성공")
                        }.onFailure {
                            Timber.e("토큰 초기화 실패")
                        }
                }.onFailure { errorType: ErrorType ->
                    handleError(errorType, MyPageRetryAction.WITHDRAW)
                    Timber.e("회원탈퇴 실패 ")
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
