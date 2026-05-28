package com.on.turip.feature.mypage.impl.viewmodel

import androidx.compose.runtime.Immutable
import com.on.turip.core.model.Content
import com.on.turip.core.model.Member
import com.on.turip.core.ui.UiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class MyPageState(
    val profile: Member? = null,
    val isProfileLoading: Boolean = true,
    val isProfileError: Boolean = false,
    val bookmarks: ImmutableList<Content> = persistentListOf(),
    val isBookmarksLoading: Boolean = true,
    val isBookmarksError: Boolean = false,
    val dialogState: MyPageDialogState? = null,
) : UiState

enum class MyPageDialogState {
    LogoutRequired,
    ConfirmWithdraw,
}
