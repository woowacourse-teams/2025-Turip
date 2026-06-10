package com.on.turip.feature.search.impl.regionresult

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.model.content.Content
import com.on.turip.core.model.content.video.VideoData
import com.on.turip.core.model.creator.Creator
import com.on.turip.core.model.region.City
import com.on.turip.feature.search.impl.component.SearchResultList
import com.on.turip.feature.search.impl.model.TripDurationModel
import com.on.turip.feature.search.impl.model.TripModel
import com.on.turip.feature.search.impl.model.VideoInformationModel
import com.on.turip.feature.search.impl.regionresult.component.RegionResultAppBar
import kotlinx.collections.immutable.persistentListOf

@Composable
fun RegionResultScreen(
    regionCategoryName: String,
    onBackClick: () -> Unit,
    onNavigateToTripDetail: (id: Long) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Surface(
            color = TuripTheme.colors.white,
            modifier = Modifier.statusBarsPadding(),
        ) {
            RegionResultAppBar(
                title = regionCategoryName,
                onBackClick = onBackClick,
            )
        }

        SearchResultList(
            totalCount = sampleRegionVideos.size,
            videos = sampleRegionVideos,
            onItemClick = onNavigateToTripDetail,
        )
    }
}

private val sampleRegionVideos =
    persistentListOf(
        VideoInformationModel(
            content =
                Content(
                    id = 10L,
                    creator = Creator(1L, "Turip", ""),
                    videoData = VideoData("지역별 추천 여행", "thumbnail", "2026-02-23"),
                    city = City("대구"),
                    isBookmarked = false,
                ),
            tripModel = TripModel(TripDurationModel(0, 1), 4),
        ),
    )

@Preview(showBackground = true)
@Composable
private fun RegionResultScreenPreview() {
    TuripTheme {
        RegionResultScreen(
            regionCategoryName = "대구",
            onBackClick = {},
            onNavigateToTripDetail = {},
            onNavigateToLogin = {},
        )
    }
}
