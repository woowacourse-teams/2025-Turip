package com.on.turip.feature.search.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.on.turip.core.designsystem.generated.resources.*
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.util.TuripUrlConverter.convertVideoThumbnailUrl
import com.on.turip.core.ui.util.formatResource
import com.on.turip.feature.search.impl.model.VideoInformationModel
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchResultList(
    totalCount: Int,
    videos: ImmutableList<VideoInformationModel>,
    onItemClick: (id: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(TuripTheme.colors.white),
    ) {
        Text(
            text = stringResource(Res.string.search_result_exist_result).formatResource(totalCount),
            style = TuripTheme.typography.body2,
            color = TuripTheme.colors.gray03,
            modifier =
                Modifier.padding(
                    horizontal = TuripTheme.spacing.extraLarge,
                    vertical = TuripTheme.spacing.medium,
                ),
        )

        LazyColumn(
            contentPadding = PaddingValues(bottom = TuripTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.large),
        ) {
            items(
                items = videos,
                key = { it.content.id },
            ) { videoInfo ->
                val videoId: Long = videoInfo.content.id

                SearchResultItem(
                    videoInfo = videoInfo,
                    onItemClick = { onItemClick(videoId) },
                )
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    videoInfo: VideoInformationModel,
    onItemClick: () -> Unit,
) {
    val itemShape = TuripTheme.shape.wideButton

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = TuripTheme.spacing.extraLarge,
                    vertical = TuripTheme.spacing.small,
                ).background(
                    color = TuripTheme.colors.cardBackground,
                    shape = itemShape,
                ).border(
                    width = 1.dp,
                    color = TuripTheme.colors.cardBorder,
                    shape = itemShape,
                ).clip(itemShape)
                .clickable { onItemClick() }
                .padding(TuripTheme.spacing.medium),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            AsyncImage(
                model = videoInfo.content.creator.profileImage,
                contentDescription = null,
                modifier =
                    Modifier
                        .size(42.dp)
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
                    text = videoInfo.content.videoData.title,
                    style = TuripTheme.typography.title2,
                    color = TuripTheme.colors.black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text =
                        stringResource(
                            Res.string.region_result_video_description,
                            videoInfo.content.creator.channelName,
                            videoInfo.content.videoData.uploadedDate,
                        ),
                    style = TuripTheme.typography.info2,
                    color = TuripTheme.colors.gray03,
                    modifier = Modifier.padding(top = TuripTheme.spacing.extraSmall),
                )
            }
        }

        Row(
            modifier =
                Modifier
                    .padding(top = TuripTheme.spacing.medium)
                    .fillMaxWidth()
                    .height(110.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = convertVideoThumbnailUrl(videoInfo.content.videoData.url),
                contentDescription = null,
                modifier =
                    Modifier
                        .size(width = 202.dp, height = 110.dp)
                        .clip(TuripTheme.shape.container),
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.width(TuripTheme.spacing.extraLarge))

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(vertical = TuripTheme.spacing.medium),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Surface(
                    color = TuripTheme.colors.chipBackground,
                    shape = TuripTheme.shape.wideButton,
                ) {
                    Text(
                        text = videoInfo.content.city.name,
                        style = TuripTheme.typography.info1,
                        color = TuripTheme.colors.gray05,
                        modifier =
                            Modifier.padding(
                                horizontal = TuripTheme.spacing.medium,
                                vertical = TuripTheme.spacing.extraSmall,
                            ),
                    )
                }

                InfoIconText(
                    iconRes = Res.drawable.ic_calendar,
                    text = videoInfo.tripModel.tripDurationModel.toDisplayText(),
                )

                InfoIconText(
                    iconRes = Res.drawable.ic_location,
                    text =
                        stringResource(Res.string.all_total_place_count)
                            .formatResource(videoInfo.tripModel.tripPlaceCount),
                )
            }
        }
    }
}

@Composable
private fun InfoIconText(
    iconRes: DrawableResource,
    text: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
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
            modifier = Modifier.padding(start = 10.dp),
        )
    }
}
