package com.on.turip.feature.mypage.impl

import androidx.compose.runtime.Immutable
import com.on.turip.core.model.bookmark.BookmarkContent
import com.on.turip.feature.mypage.impl.model.ProfileModel
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class MyPageUiState(
    val profileState: MyPageSectionState<ProfileModel>,
    val bookmarkContentState: MyPageSectionState<ImmutableList<BookmarkContent>>,
    val dialogState: MyPageDialogState?,
    val isLoggingOut: Boolean = false,
) {
    companion object {
        val Idle: MyPageUiState =
            MyPageUiState(
                profileState = MyPageSectionState.Loading,
                bookmarkContentState = MyPageSectionState.Loading,
                dialogState = null,
            )
    }
}
