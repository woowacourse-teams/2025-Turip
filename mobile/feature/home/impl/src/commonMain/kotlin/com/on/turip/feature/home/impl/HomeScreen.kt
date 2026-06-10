package com.on.turip.feature.home.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.core.model.content.Content
import com.on.turip.core.model.content.video.VideoData
import com.on.turip.core.model.creator.Creator
import com.on.turip.core.model.region.City
import com.on.turip.core.model.region.Country
import com.on.turip.core.model.region.RegionCategory
import com.on.turip.core.model.trip.TripDuration
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.home.impl.component.HomeAppBar
import com.on.turip.feature.home.impl.component.RegionList
import com.on.turip.feature.home.impl.component.RegionTypeButtons
import com.on.turip.feature.home.impl.component.SearchTextField
import com.on.turip.feature.home.impl.component.UsersLikeList
import com.on.turip.feature.home.impl.model.UsersLikeContentModel

@Composable
fun HomeScreen(
    onSearchClick: (keyword: String) -> Unit,
    onRegionClick: (regionName: String) -> Unit,
    onContentClick: (contentId: Long) -> Unit,
    onNavigateToLoginScreen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HomeScreenContent(
        usersLikeContents = previewUsersLikeContents,
        domesticRegions = previewDomesticRegions,
        abroadRegions = previewAbroadRegions,
        onSearchClick = onSearchClick,
        onContentClick = { onContentClick(it.content.id) },
        onRegionClick = onRegionClick,
        modifier = modifier,
    )
}

@Composable
private fun HomeScreenContent(
    usersLikeContents: List<UsersLikeContentModel>,
    domesticRegions: List<RegionCategory>,
    abroadRegions: List<RegionCategory>,
    onSearchClick: (keyword: String) -> Unit,
    onContentClick: (usersLikeContentModel: UsersLikeContentModel) -> Unit,
    onRegionClick: (regionName: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var keyword: String by rememberSaveable { mutableStateOf("") }
    var isDomesticSelected: Boolean by rememberSaveable { mutableStateOf(true) }
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = modifier.fillMaxSize()) {
        Spacer(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .systemBarsPadding()
                    .background(TuripTheme.colors.white),
        )
        HomeAppBar()
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        })
                    }.padding(horizontal = TuripTheme.spacing.extraLarge)
                    .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
        ) {
            Text(
                text = "😊 오늘의 기분은, 여행하기 딱 좋아요!",
                color = TuripTheme.colors.gray04,
                style = TuripTheme.typography.title1,
            )

            SearchTextField(
                keyword = keyword,
                onKeywordChange = { newKeyword -> keyword = newKeyword },
                onSearch = { searchKeyword ->
                    onSearchClick(searchKeyword)
                    focusManager.clearFocus(force = true)
                    keyboardController?.hide()
                },
                modifier =
                    Modifier
                        .wrapContentSize()
                        .padding(top = TuripTheme.spacing.extraSmall),
            )

            Text(
                text = "❤️ 한 주간 가장 많이 북마크 된 콘텐츠",
                modifier = Modifier.padding(top = TuripTheme.spacing.medium),
                color = TuripTheme.colors.gray04,
                style = TuripTheme.typography.title1,
            )

            UsersLikeList(
                usersLikeContents = usersLikeContents,
                onContentClick = onContentClick,
            )

            RegionTypeButtons(
                onDomesticClick = { isDomesticSelected = it },
                isSelectedDomestic = isDomesticSelected,
            )

            RegionList(
                regions = if (isDomesticSelected) domesticRegions else abroadRegions,
                onRegionClick = onRegionClick,
                modifier = Modifier.padding(bottom = TuripTheme.spacing.large),
            )
        }
    }
}

private val previewUsersLikeContents =
    listOf(
        previewUsersLikeContent(1L, "대프리카지만 괜찮아... 느좋 대구 소품샵 카페 투어입니당", "대구"),
        previewUsersLikeContent(2L, "바다 따라 걷는 제주 여행 코스", "제주"),
        previewUsersLikeContent(3L, "도쿄 골목 산책과 맛집 투어", "도쿄"),
    )

private fun previewUsersLikeContent(
    id: Long,
    title: String,
    city: String,
): UsersLikeContentModel =
    UsersLikeContentModel(
        content =
            Content(
                id = id,
                creator = Creator(id = id, channelName = "여행하는 튜립팀", profileImage = ""),
                videoData =
                    VideoData(
                        title = title,
                        url = "https://youtu.be/dQw4w9WgXcQ",
                        uploadedDate = "2026-02-23",
                    ),
                city = City(name = city),
                isBookmarked = true,
            ),
        tripDuration = TripDuration(nights = 1, days = 2),
    )

private val previewDomesticRegions =
    listOf("서울", "부산", "제주", "강릉", "경주", "여수", "속초", "전주").mapIndexed { index, name ->
        RegionCategory(name = name, imageUrl = "", country = Country(index, "대한민국", ""))
    }

private val previewAbroadRegions =
    listOf("도쿄", "오사카", "후쿠오카", "방콕", "타이베이", "다낭", "파리", "런던").mapIndexed { index, name ->
        RegionCategory(name = name, imageUrl = "", country = Country(index, "해외", ""))
    }

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    TuripTheme {
        HomeScreenContent(
            usersLikeContents = previewUsersLikeContents,
            domesticRegions = previewDomesticRegions,
            abroadRegions = previewAbroadRegions,
            onSearchClick = {},
            onContentClick = {},
            onRegionClick = {},
        )
    }
}
