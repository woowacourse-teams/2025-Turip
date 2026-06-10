package com.on.turip.feature.mypage.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.domain.session.SessionState
import com.on.turip.core.model.bookmark.BookmarkContent
import com.on.turip.core.model.content.Content
import com.on.turip.core.model.content.video.VideoData
import com.on.turip.core.model.creator.Creator
import com.on.turip.core.model.region.City
import com.on.turip.core.model.trip.TripDuration
import com.on.turip.feature.mypage.impl.component.MyPageAppBar
import com.on.turip.feature.mypage.impl.component.MyPageBookmarkContentSection
import com.on.turip.feature.mypage.impl.component.MyPageSettingsSection
import com.on.turip.feature.mypage.impl.component.ProfileSection
import com.on.turip.feature.mypage.impl.model.ProfileModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun MyPageScreen(
    onNavigateToAllBookmarkContents: () -> Unit,
    onNavigateToContent: (contentId: Long) -> Unit,
    onNavigateToInquiry: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MyPageScreenContent(
        profileState =
            MyPageSectionState.Success(
                ProfileModel(
                    id = 0L,
                    nickname = "게스트",
                    imageUrl = null,
                ),
            ),
        bookmarkContentState = MyPageSectionState.Success(sampleBookmarkContents),
        sessionState = SessionState.Guest,
        onNavigateToAllBookmarkContents = onNavigateToAllBookmarkContents,
        onNavigateToContent = onNavigateToContent,
        onNavigateToInquiry = onNavigateToInquiry,
        onNavigateToPrivacyPolicy = onNavigateToPrivacyPolicy,
        onNavigateToLogin = onNavigateToLogin,
        onProfileRetry = {},
        onBookmarkRetry = {},
        onRemoveBookmark = {},
        onLogoutClick = {},
        onWithdrawClick = {},
        modifier = modifier,
    )
}

@Composable
private fun MyPageScreenContent(
    profileState: MyPageSectionState<ProfileModel>,
    bookmarkContentState: MyPageSectionState<ImmutableList<BookmarkContent>>,
    sessionState: SessionState,
    onNavigateToAllBookmarkContents: () -> Unit,
    onNavigateToContent: (contentId: Long) -> Unit,
    onNavigateToInquiry: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onProfileRetry: () -> Unit,
    onBookmarkRetry: () -> Unit,
    onRemoveBookmark: (contentId: Long) -> Unit,
    onLogoutClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .background(TuripTheme.colors.white)
                .systemBarsPadding(),
        contentPadding =
            PaddingValues(
                bottom = TuripTheme.spacing.medium,
            ),
        verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraExtraLarge),
    ) {
        item {
            MyPageAppBar()
        }

        item {
            ProfileSection(
                state = profileState,
                onRetry = onProfileRetry,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = TuripTheme.spacing.extraLarge,
                            vertical = TuripTheme.spacing.small,
                        ),
            )
        }

        item {
            MyPageBookmarkContentSection(
                state = bookmarkContentState,
                onViewAllContentClick = onNavigateToAllBookmarkContents,
                onContentClick = onNavigateToContent,
                onRemoveBookmark = onRemoveBookmark,
                onRetry = onBookmarkRetry,
            )
        }

        item {
            MyPageSettingsSection(
                sessionState = sessionState,
                onInquiryClick = onNavigateToInquiry,
                onPrivacyPolicyClick = onNavigateToPrivacyPolicy,
                onLoginClick = onNavigateToLogin,
                onLogoutClick = onLogoutClick,
                onWithdrawClick = onWithdrawClick,
                modifier = Modifier.padding(horizontal = TuripTheme.spacing.medium),
            )
        }
    }
}

private val sampleBookmarkContents: ImmutableList<BookmarkContent> =
    persistentListOf(
        BookmarkContent(
            bookmarkId = 1L,
            content =
                Content(
                    id = 1L,
                    creator = Creator(1L, "여행 크리에이터", ""),
                    videoData =
                        VideoData(
                            title = "콘텐츠 제목이 길면 말줄임 처리되는지 확인합니다",
                            url = "https://img.youtube.com/vi/sample/default.jpg",
                            uploadedDate = "2026-02-23",
                        ),
                    city = City("제주"),
                    isBookmarked = true,
                ),
            tripDuration = TripDuration(1, 2),
            tripPlaceCount = 3,
            createdAt = "2026-03-04",
        ),
        BookmarkContent(
            bookmarkId = 2L,
            content =
                Content(
                    id = 2L,
                    creator = Creator(2L, "Turip", ""),
                    videoData =
                        VideoData(
                            title = "짧은 제목",
                            url = "",
                            uploadedDate = "2026-02-24",
                        ),
                    city = City("서울"),
                    isBookmarked = true,
                ),
            tripDuration = TripDuration(0, 1),
            tripPlaceCount = 5,
            createdAt = "2026-03-05",
        ),
    )

@Preview(showBackground = true)
@Composable
private fun MyPageScreenPreview() {
    TuripTheme {
        MyPageScreenContent(
            profileState =
                MyPageSectionState.Success(
                    ProfileModel(
                        id = 0L,
                        nickname = "게스트",
                        imageUrl = null,
                    ),
                ),
            bookmarkContentState = MyPageSectionState.Success(sampleBookmarkContents),
            sessionState = SessionState.Guest,
            onNavigateToAllBookmarkContents = {},
            onNavigateToContent = {},
            onNavigateToInquiry = {},
            onNavigateToPrivacyPolicy = {},
            onNavigateToLogin = {},
            onProfileRetry = {},
            onBookmarkRetry = {},
            onRemoveBookmark = {},
            onLogoutClick = {},
            onWithdrawClick = {},
        )
    }
}
