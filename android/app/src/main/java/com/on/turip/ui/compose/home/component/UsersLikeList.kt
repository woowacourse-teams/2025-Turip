package com.on.turip.ui.compose.home.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.domain.content.Content
import com.on.turip.domain.content.video.VideoData
import com.on.turip.domain.creator.Creator
import com.on.turip.domain.region.City
import com.on.turip.ui.common.TuripUrlConverter
import com.on.turip.ui.common.model.trip.TripDurationModel
import com.on.turip.ui.common.model.trip.toDisplayText
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.main.home.model.UsersLikeContentModel

@Composable
fun UsersLikeList(
    usersLikeContents: List<UsersLikeContentModel>,
    onContentClick: (UsersLikeContentModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.wrapContentHeight(),
        contentPadding = PaddingValues(end = TuripTheme.spacing.small),
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
    ) {
        items(
            usersLikeContents,
            key = { it.content.id },
        ) { usersLikeContent: UsersLikeContentModel ->
            UsersLikeItem(
                thumbnailUrl = TuripUrlConverter.convertVideoThumbnailUrl(usersLikeContent.content.videoData.url),
                regionName = usersLikeContent.content.city.name,
                title = usersLikeContent.content.videoData.title,
                channelName = usersLikeContent.content.creator.channelName,
                contentDescription =
                    stringResource(
                        R.string.all_video_description,
                        usersLikeContent.content.videoData.uploadedDate,
                        usersLikeContent.tripDuration.toDisplayText(LocalContext.current),
                    ),
                modifier =
                    Modifier
                        .width(280.dp)
                        .clip(TuripTheme.shape.chip)
                        .clickable { onContentClick(usersLikeContent) },
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 1000)
@Composable
private fun UsersLikeListPreview() {
    TuripTheme {
        UsersLikeList(
            usersLikeContents = previewUsersLikeContents(),
            onContentClick = {},
        )
    }
}

private fun previewUsersLikeContents(): List<UsersLikeContentModel> =
    listOf(
        previewUsersLikeContent(
            id = 1,
            title = "채넛과 함께하는 즐거운 여행",
            city = "속초",
        ),
        previewUsersLikeContent(
            id = 2,
            title = "뭉치와 떠나는 힐링 제주 여행",
            city = "제주",
        ),
        previewUsersLikeContent(
            id = 3,
            title = "제리와 함께하는 부산 바다 먹방 여행 코스",
            city = "부산",
        ),
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
                creator =
                    Creator(
                        id = id,
                        channelName = "채넛",
                        profileImage = "",
                    ),
                videoData =
                    VideoData(
                        title = title,
                        url = "",
                        uploadedDate = "2025-02-16",
                    ),
                city =
                    City(
                        name = city,
                    ),
                isFavorite = true,
            ),
        tripDuration =
            TripDurationModel(
                nights = 1,
                days = 2,
            ),
    )
