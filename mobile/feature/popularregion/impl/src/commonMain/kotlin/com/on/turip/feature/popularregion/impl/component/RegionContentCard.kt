package com.on.turip.feature.popularregion.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_total_place_count
import com.on.turip.core.designsystem.generated.resources.all_trip_day_trip_duration
import com.on.turip.core.designsystem.generated.resources.all_trip_duration
import com.on.turip.core.designsystem.generated.resources.all_video_description
import com.on.turip.core.designsystem.generated.resources.ic_calendar
import com.on.turip.core.designsystem.generated.resources.ic_location
import com.on.turip.core.designsystem.generated.resources.popular_region_bookmark_description
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.util.formatResource
import com.on.turip.feature.popularregion.impl.model.RegionContentModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * 바텀시트 안의 연관 콘텐츠 카드.
 *
 * 썸네일이 아직 안 왔거나 실패해도 카드가 비어 보이지 않도록
 * 이미지 뒤에 옅은 그라데이션을 깔아둔다.
 */
@Composable
internal fun RegionContentCard(
    content: RegionContentModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardShape = TuripTheme.shape.largeContainer

    Column(
        modifier =
            modifier
                .width(CARD_WIDTH)
                .background(color = TuripTheme.colors.white, shape = cardShape)
                .border(
                    width = CARD_BORDER_WIDTH,
                    color = TuripTheme.colors.cardBorder,
                    shape = cardShape,
                ).clip(cardShape)
                .clickable(onClick = onClick)
                .padding(TuripTheme.spacing.small),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(THUMBNAIL_HEIGHT)
                    .clip(TuripTheme.shape.container)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                PopularRegionMapPalette.HeatLow,
                                PopularRegionMapPalette.Sea,
                            ),
                        ),
                    ),
        ) {
            AsyncImage(
                model = content.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Icon(
                imageVector =
                    if (content.isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                contentDescription = stringResource(Res.string.popular_region_bookmark_description),
                tint = if (content.isBookmarked) TuripTheme.colors.primary else Color.White,
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(TuripTheme.spacing.small)
                        .size(BOOKMARK_ICON_SIZE),
            )
        }

        Text(
            text = content.title,
            style = TuripTheme.typography.title3,
            color = TuripTheme.colors.black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = TuripTheme.spacing.small),
        )

        Text(
            text =
                stringResource(Res.string.all_video_description)
                    .formatResource(content.creatorName, content.uploadedDate),
            style = TuripTheme.typography.info2,
            color = TuripTheme.colors.gray03,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = TuripTheme.spacing.extraSmall),
        )

        Row(
            modifier = Modifier.padding(top = TuripTheme.spacing.small),
            horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ContentInfoIconText(
                iconRes = Res.drawable.ic_calendar,
                text = content.durationText(),
            )
            ContentInfoIconText(
                iconRes = Res.drawable.ic_location,
                text =
                    stringResource(Res.string.all_total_place_count)
                        .formatResource(content.placeCount),
            )
        }
    }
}

@Composable
private fun RegionContentModel.durationText(): String =
    if (isDayTrip) {
        stringResource(Res.string.all_trip_day_trip_duration)
    } else {
        stringResource(Res.string.all_trip_duration).formatResource(nights, days)
    }

@Composable
private fun ContentInfoIconText(
    iconRes: DrawableResource,
    text: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = TuripTheme.colors.gray02,
            modifier = Modifier.size(INFO_ICON_SIZE),
        )
        Text(
            text = text,
            style = TuripTheme.typography.info2,
            color = TuripTheme.colors.gray04,
            modifier = Modifier.padding(start = TuripTheme.spacing.extraSmall),
        )
    }
}

private val CARD_WIDTH = 220.dp
private val THUMBNAIL_HEIGHT = 124.dp
private val BOOKMARK_ICON_SIZE = 22.dp
private val INFO_ICON_SIZE = 14.dp
private val CARD_BORDER_WIDTH = 1.dp

@Preview(showBackground = true)
@Composable
private fun RegionContentCardPreview() {
    TuripTheme {
        RegionContentCard(
            content =
                RegionContentModel(
                    contentId = 1L,
                    title = "강릉 바다 따라 힐링 여행 브이로그",
                    thumbnailUrl = "",
                    creatorName = "바다좋아 튜립팀",
                    uploadedDate = "2026-05-12",
                    nights = 2,
                    days = 3,
                    placeCount = 15,
                    isBookmarked = true,
                ),
            onClick = {},
            modifier = Modifier.padding(TuripTheme.spacing.large),
        )
    }
}
