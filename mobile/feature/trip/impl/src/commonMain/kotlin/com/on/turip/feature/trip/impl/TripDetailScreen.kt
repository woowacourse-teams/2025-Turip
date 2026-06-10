package com.on.turip.feature.trip.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.trip.impl.component.ContentBookmarkButton
import com.on.turip.feature.trip.impl.component.ContentInformation
import com.on.turip.feature.trip.impl.component.CreatorInformation
import com.on.turip.feature.trip.impl.component.PlaceItem
import com.on.turip.feature.trip.impl.component.TripDetailAppBar
import com.on.turip.feature.trip.impl.model.MapModel
import com.on.turip.feature.trip.impl.model.PlaceModel
import com.on.turip.feature.trip.impl.model.TripDetailInfoModel
import com.on.turip.feature.trip.impl.model.TripDurationModel
import kotlinx.collections.immutable.persistentListOf

@Composable
fun TripDetailScreen(
    contentId: Long,
    navigateToBack: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToTuripDetail: (turipId: Long) -> Unit,
    navigateToMap: (mapModel: MapModel) -> Unit,
    navigateToWebViewUrl: (url: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Spacer(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.systemBars)
                    .background(TuripTheme.colors.primary),
        )
        TripDetailAppBar(
            isError = false,
            isLoading = false,
            isBookmarked = true,
            onBackClick = navigateToBack,
            onBookmarkClick = {},
        )

        TripDetailScreenContent(
            information = sampleTripDetailInfo,
            places = samplePlaces,
            isBookmarked = true,
            onTimeLineClick = {},
            onMapClick = navigateToMap,
            onTuripPlaceClick = { _, _ -> },
            onBookmarkClick = {},
            onErrorVideoClick = { navigateToWebViewUrl(sampleTripDetailInfo.videoLink) },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun TripDetailScreenContent(
    information: TripDetailInfoModel,
    places: List<PlaceModel>,
    isBookmarked: Boolean,
    onTimeLineClick: (timeLine: Int) -> Unit,
    onMapClick: (mapModel: MapModel) -> Unit,
    onTuripPlaceClick: (id: Long, placeName: String) -> Unit,
    onBookmarkClick: () -> Unit,
    onErrorVideoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.background(TuripTheme.colors.white)) {
        item {
            VideoPlaceholder(onClick = onErrorVideoClick)
        }

        item {
            CreatorInformation(
                thumbnailUrl = information.creatorThumbnail,
                name = information.creatorName,
            )
        }

        item {
            ContentInformation(
                information = information,
                modifier = Modifier.padding(top = TuripTheme.spacing.large),
            )
        }

        item {
            ContentBookmarkButton(
                isBookmarked = isBookmarked,
                onClick = onBookmarkClick,
                modifier =
                    Modifier
                        .padding(horizontal = TuripTheme.spacing.extraLarge)
                        .padding(top = TuripTheme.spacing.extraLarge),
            )
        }

        items(places, key = { it.id }) { place ->
            PlaceItem(
                placeModel = place,
                onTimeLineClick = onTimeLineClick,
                onMapClick = onMapClick,
                onTuripPlaceClick = onTuripPlaceClick,
                modifier =
                    Modifier
                        .padding(horizontal = TuripTheme.spacing.extraLarge)
                        .padding(top = TuripTheme.spacing.medium),
            )
        }

        item {
            Spacer(modifier = Modifier.height(TuripTheme.spacing.extraHuge))
        }
    }
}

@Composable
private fun VideoPlaceholder(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
                .background(TuripTheme.colors.black),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "YouTube",
            style = TuripTheme.typography.display,
            color = TuripTheme.colors.white,
            textAlign = TextAlign.Center,
        )
    }
}

private val sampleTripDetailInfo =
    TripDetailInfoModel(
        creatorName = "여행하는 튜립팀",
        creatorThumbnail = "",
        city = "서울",
        videoLink = "",
        contentTitle = "서울 당일치기 여행 코스",
        uploadedDate = "2026-02-23",
        placeTotalCount = 3,
        duration = TripDurationModel(0, 1),
    )

private val samplePlaces =
    persistentListOf(
        PlaceModel(
            id = 1L,
            name = "안국역",
            isTuripPlace = true,
            category = "역",
            mapLink = "kakao.com/123",
            timeLine = "01:03",
        ),
        PlaceModel(
            id = 2L,
            name = "북촌한옥마을",
            isTuripPlace = false,
            category = "명소",
            mapLink = "google.com/maps",
            timeLine = "03:12",
        ),
    )

@Preview(showBackground = true)
@Composable
private fun TripDetailScreenPreview() {
    TuripTheme {
        TripDetailScreen(
            contentId = 1L,
            navigateToBack = {},
            navigateToLogin = {},
            navigateToTuripDetail = {},
            navigateToMap = {},
            navigateToWebViewUrl = {},
        )
    }
}
