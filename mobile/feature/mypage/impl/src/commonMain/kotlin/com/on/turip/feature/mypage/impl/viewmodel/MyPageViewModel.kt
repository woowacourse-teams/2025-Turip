package com.on.turip.feature.mypage.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.on.turip.core.domain.repository.AccountRepository
import com.on.turip.core.domain.repository.BookmarkRepository
import com.on.turip.core.domain.session.SessionManager
import com.on.turip.core.ui.BaseViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

private const val PRIVACY_POLICY_URL = "https://turip.co.kr/privacy"
private const val MAX_BOOKMARK_DISPLAY = 10
private const val PAGE_SIZE = 20

class MyPageViewModel(
    private val accountRepository: AccountRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val sessionManager: SessionManager,
) : BaseViewModel<MyPageIntent, MyPageState, MyPageEffect>(MyPageState()) {

    private val pendingRemovals = mutableMapOf<Long, com.on.turip.core.model.Content>()

    init {
        loadProfile()
        loadBookmarks()
    }

    override fun onIntent(intent: MyPageIntent) {
        when (intent) {
            MyPageIntent.LoadProfile -> loadProfile()
            MyPageIntent.LoadBookmarks -> loadBookmarks()
            is MyPageIntent.RemoveBookmark -> removeBookmark(intent.contentId)
            is MyPageIntent.RollbackRemove -> rollbackRemove(intent.contentId)
            MyPageIntent.ClickInquiry -> emitEffect(MyPageEffect.NavigateToInquiry("", ""))
            MyPageIntent.ClickPrivacyPolicy -> emitEffect(MyPageEffect.NavigateToPrivacyPolicy(PRIVACY_POLICY_URL))
            MyPageIntent.ClickLogout -> updateState { copy(dialogState = MyPageDialogState.LogoutRequired) }
            MyPageIntent.ClickWithdraw -> updateState { copy(dialogState = MyPageDialogState.ConfirmWithdraw) }
            MyPageIntent.ConfirmLogout -> confirmLogout()
            MyPageIntent.ConfirmWithdraw -> confirmWithdraw()
            MyPageIntent.DismissDialog -> updateState { copy(dialogState = null) }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            updateState { copy(isProfileLoading = true, isProfileError = false) }
            accountRepository.getMyProfile()
                .onSuccess { member -> updateState { copy(profile = member, isProfileLoading = false) } }
                .onFailure {
                    updateState { copy(isProfileLoading = false, isProfileError = true) }
                    emitEffect(MyPageEffect.ShowProfileLoadFailed)
                }
        }
    }

    private fun loadBookmarks() {
        viewModelScope.launch {
            updateState { copy(isBookmarksLoading = true, isBookmarksError = false) }
            bookmarkRepository.getBookmarks(size = MAX_BOOKMARK_DISPLAY, lastId = 0)
                .onSuccess { contents ->
                    updateState {
                        copy(
                            bookmarks = contents.take(MAX_BOOKMARK_DISPLAY).toImmutableList(),
                            isBookmarksLoading = false,
                        )
                    }
                }
                .onFailure {
                    updateState { copy(isBookmarksLoading = false, isBookmarksError = true) }
                    emitEffect(MyPageEffect.ShowBookmarksLoadFailed)
                }
        }
    }

    private fun removeBookmark(contentId: Long) {
        val item = currentState.bookmarks.find { it.id == contentId } ?: return
        pendingRemovals[contentId] = item
        updateState { copy(bookmarks = bookmarks.filter { it.id != contentId }.toImmutableList()) }
        viewModelScope.launch {
            bookmarkRepository.removeBookmark(contentId)
                .onSuccess { pendingRemovals.remove(contentId) }
                .onFailure { emitEffect(MyPageEffect.ShowRemoveFailedSnackbar(contentId)) }
        }
    }

    private fun rollbackRemove(contentId: Long) {
        val item = pendingRemovals.remove(contentId) ?: return
        if (currentState.bookmarks.any { it.id == contentId }) return
        updateState { copy(bookmarks = (bookmarks + item).sortedByDescending { it.id }.toImmutableList()) }
    }

    private fun confirmLogout() {
        updateState { copy(dialogState = null) }
        viewModelScope.launch {
            sessionManager.switchToGuest()
            emitEffect(MyPageEffect.NavigateToLogin)
        }
    }

    private fun confirmWithdraw() {
        updateState { copy(dialogState = null) }
        viewModelScope.launch {
            accountRepository.deleteAccount()
                .onSuccess {
                    sessionManager.switchToGuest()
                    emitEffect(MyPageEffect.NavigateToLogin)
                }
                .onFailure { emitEffect(MyPageEffect.ShowWithdrawError) }
        }
    }
}
