package com.on.turip.ui.compose.mypage

import androidx.compose.runtime.Immutable
import com.on.turip.domain.bookmark.BookmarkContent
import com.on.turip.ui.compose.mypage.model.MyPageSectionState
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class MyPageUiState(
    val profileState: MyPageSectionState<Unit>,
    val bookmarkContentState: MyPageSectionState<ImmutableList<BookmarkContent>>,
) {
    companion object {
        val Idle: MyPageUiState =
            MyPageUiState(
                profileState = MyPageSectionState.Loading,
                bookmarkContentState = MyPageSectionState.Loading,
            )
    }
}
