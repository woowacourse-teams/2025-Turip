package com.on.turip.core.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_total_place_count
import com.on.turip.core.designsystem.generated.resources.all_trip_day_trip_duration
import com.on.turip.core.designsystem.generated.resources.all_trip_duration
import com.on.turip.core.designsystem.generated.resources.all_video_description
import com.on.turip.core.designsystem.generated.resources.briefing_selected_video
import com.on.turip.core.designsystem.generated.resources.ic_calendar
import com.on.turip.core.designsystem.generated.resources.ic_location
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.model.content.VideoSummaryModel
import com.on.turip.core.ui.util.formatResource
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * 브리핑 본문의 영상 카드.
 *
 * [isSelected] 는 고른 영상을 다음 단계로 넘기는 화면(랜덤 여행)에서만 쓴다.
 * 탭이 곧 이동인 화면은 기본값 그대로 두면 선택 표시가 나오지 않는다.
 */
@Composable
fun VideoSummaryItem(
    video: VideoSummaryModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
) {
    val itemShape = TuripTheme.shape.wideButton
    val borderColor: Color by animateColorAsState(
        targetValue = if (isSelected) TuripTheme.colors.primary else TuripTheme.colors.cardBorder,
    )
    val backgroundColor: Color by animateColorAsState(
        targetValue =
            if (isSelected) TuripTheme.colors.primarySub else TuripTheme.colors.cardBackground,
    )

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(color = backgroundColor, shape = itemShape)
                .border(
                    width = if (isSelected) SELECTED_BORDER_WIDTH else DEFAULT_BORDER_WIDTH,
                    color = borderColor,
                    shape = itemShape,
                ).clip(itemShape)
                .clickable(onClick = onClick)
                .padding(TuripTheme.spacing.medium),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            AsyncImage(
                model = video.profileImageUrl,
                contentDescription = null,
                modifier =
                    Modifier
                        .size(PROFILE_IMAGE_SIZE)
                        .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )

            Column(
                modifier =
                    Modifier
                        .padding(start = TuripTheme.spacing.extraSmall)
                        .weight(1f),
            ) {
                Text(
                    text = video.title,
                    style = TuripTheme.typography.title2,
                    color = TuripTheme.colors.black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text =
                        stringResource(
                            Res.string.all_video_description,
                            video.channelName,
                            video.uploadedDate,
                        ),
                    style = TuripTheme.typography.info2,
                    color = TuripTheme.colors.gray03,
                    modifier = Modifier.padding(top = TuripTheme.spacing.extraSmall),
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = stringResource(Res.string.briefing_selected_video),
                    tint = TuripTheme.colors.primary,
                    modifier =
                        Modifier
                            .padding(start = TuripTheme.spacing.extraSmall)
                            .size(SELECTED_ICON_SIZE),
                )
            }
        }

        Row(
            modifier =
                Modifier
                    .padding(top = TuripTheme.spacing.medium)
                    .fillMaxWidth()
                    .height(THUMBNAIL_HEIGHT),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = null,
                modifier =
                    Modifier
                        .size(width = THUMBNAIL_WIDTH, height = THUMBNAIL_HEIGHT)
                        .clip(TuripTheme.shape.container),
                contentScale = ContentScale.Crop,
            )

            Column(
                modifier =
                    Modifier
                        .padding(start = TuripTheme.spacing.large)
                        .weight(1f)
                        .height(THUMBNAIL_HEIGHT)
                        .padding(vertical = TuripTheme.spacing.small),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Surface(
                    color = TuripTheme.colors.chipBackground,
                    shape = TuripTheme.shape.wideButton,
                ) {
                    Text(
                        text = video.cityName,
                        style = TuripTheme.typography.info1,
                        color = TuripTheme.colors.gray05,
                        modifier =
                            Modifier.padding(
                                horizontal = TuripTheme.spacing.medium,
                                vertical = TuripTheme.spacing.extraSmall,
                            ),
                    )
                }

                VideoInfoIconText(
                    iconRes = Res.drawable.ic_calendar,
                    text = video.durationText(),
                )

                VideoInfoIconText(
                    iconRes = Res.drawable.ic_location,
                    text =
                        stringResource(Res.string.all_total_place_count)
                            .formatResource(video.placeCount),
                )
            }
        }
    }
}

@Composable
private fun VideoSummaryModel.durationText(): String =
    if (nights == 0) {
        stringResource(Res.string.all_trip_day_trip_duration)
    } else {
        stringResource(Res.string.all_trip_duration).formatResource(nights, days)
    }

@Composable
private fun VideoInfoIconText(
    iconRes: DrawableResource,
    text: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(TuripTheme.spacing.large),
            tint = TuripTheme.colors.gray02,
        )
        Text(
            text = text,
            style = TuripTheme.typography.info1,
            color = TuripTheme.colors.black,
            modifier = Modifier.padding(start = TuripTheme.spacing.small),
        )
    }
}

private val PROFILE_IMAGE_SIZE = 42.dp
private val THUMBNAIL_WIDTH = 160.dp
private val THUMBNAIL_HEIGHT = 90.dp
private val SELECTED_ICON_SIZE = 22.dp
private val SELECTED_BORDER_WIDTH = 2.dp
private val DEFAULT_BORDER_WIDTH = 1.dp
