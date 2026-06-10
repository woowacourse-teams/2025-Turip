package com.on.turip.feature.search.impl.keyword

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.model.content.Content
import com.on.turip.core.model.content.video.VideoData
import com.on.turip.core.model.creator.Creator
import com.on.turip.core.model.region.City
import com.on.turip.core.model.searchhistory.SearchHistory
import com.on.turip.feature.search.impl.component.SearchResultList
import com.on.turip.feature.search.impl.keyword.component.SearchAppBar
import com.on.turip.feature.search.impl.keyword.component.SearchEmptyView
import com.on.turip.feature.search.impl.keyword.component.SearchHistoryList
import com.on.turip.feature.search.impl.model.TripDurationModel
import com.on.turip.feature.search.impl.model.TripModel
import com.on.turip.feature.search.impl.model.VideoInformationModel
import kotlinx.collections.immutable.persistentListOf

@Composable
fun SearchScreen(
    keyword: String,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (id: Long) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    var searchText by remember(keyword) { mutableStateOf(keyword) }
    var isHistoryVisible by remember { mutableStateOf(false) }
    val videos = sampleVideos(searchText.ifBlank { "여행" })

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(top = TuripTheme.spacing.medium),
    ) {
        Surface(
            color = TuripTheme.colors.white,
            modifier = Modifier.statusBarsPadding(),
        ) {
            SearchAppBar(
                searchText = searchText,
                onSearchTextChanged = { searchText = it },
                onSearchAction = {
                    isHistoryVisible = false
                    focusManager.clearFocus()
                },
                onClearClick = { searchText = "" },
                onBackClick = onNavigateBack,
                onFocusChanged = { hasFocus -> isHistoryVisible = hasFocus },
            )
        }

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .addFocusCleaner(focusManager),
        ) {
            if (searchText.isBlank()) {
                SearchEmptyView(searchText)
            } else {
                SearchResultList(
                    totalCount = videos.size,
                    videos = videos,
                    onItemClick = onNavigateToDetail,
                )
            }

            if (isHistoryVisible) {
                SearchHistoryList(
                    histories =
                        persistentListOf(
                            SearchHistory("제주도 맛집", 0L),
                            SearchHistory("서울 가볼만한 곳", 0L),
                        ),
                    onHistoryClick = {
                        searchText = it
                        isHistoryVisible = false
                        focusManager.clearFocus()
                    },
                    onDeleteClick = {},
                )
            }
        }
    }
}

private fun Modifier.addFocusCleaner(focusManager: FocusManager): Modifier =
    pointerInput(Unit) {
        detectTapGestures(onTap = {
            focusManager.clearFocus()
        })
    }

private fun sampleVideos(keyword: String) =
    persistentListOf(
        VideoInformationModel(
            content =
                Content(
                    id = 1L,
                    creator = Creator(1L, "여행 크리에이터", ""),
                    videoData = VideoData("$keyword 콘텐츠 제목", "thumbnail", "2026-02-23"),
                    city = City("제주"),
                    isBookmarked = false,
                ),
            tripModel = TripModel(TripDurationModel(1, 2), 3),
        ),
    )

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    TuripTheme {
        SearchScreen(
            keyword = "제주",
            onNavigateBack = {},
            onNavigateToDetail = {},
            onNavigateToLogin = {},
        )
    }
}
