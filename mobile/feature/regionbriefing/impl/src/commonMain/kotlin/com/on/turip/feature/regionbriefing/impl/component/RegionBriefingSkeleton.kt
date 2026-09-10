package com.on.turip.feature.regionbriefing.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.component.SkeletonBox
import com.on.turip.core.designsystem.component.rememberSkeletonAlpha
import com.on.turip.core.designsystem.theme.TuripTheme

/**
 * 브리핑 영상 목록의 로딩 자리.
 *
 * `VideoSummaryItem`(core:ui) 의 배치를 그대로 흉내 낸다. 카드가 바뀌면 이쪽도 같이 손봐야 한다.
 * 미리보기로 보여주는 개수만큼만 그려서 실제 목록이 도착할 때 높이가 크게 튀지 않게 한다.
 */
@Composable
internal fun VideoListSkeleton(modifier: Modifier = Modifier) {
    val alpha: Float = rememberSkeletonAlpha(label = "regionBriefingVideoList")

    Column(
        modifier = modifier.fillMaxWidth().graphicsLayer(alpha = alpha),
        verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.large),
    ) {
        repeat(VIDEO_SKELETON_ITEM_COUNT) {
            VideoItemSkeleton()
        }
    }
}

@Composable
private fun VideoItemSkeleton(modifier: Modifier = Modifier) {
    val itemShape = TuripTheme.shape.wideButton

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(color = TuripTheme.colors.cardBackground, shape = itemShape)
                .border(
                    width = CARD_BORDER_WIDTH,
                    color = TuripTheme.colors.cardBorder,
                    shape = itemShape,
                ).padding(TuripTheme.spacing.medium),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            SkeletonBox(
                shape = CircleShape,
                modifier = Modifier.size(PROFILE_IMAGE_SIZE),
            )

            Column(
                modifier =
                    Modifier
                        .padding(start = TuripTheme.spacing.extraSmall)
                        .weight(1f),
                verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraSmall),
            ) {
                SkeletonBox(
                    shape = TuripTheme.shape.container,
                    modifier = Modifier.fillMaxWidth(TITLE_WIDTH_RATIO).height(TITLE_HEIGHT),
                )

                SkeletonBox(
                    shape = TuripTheme.shape.container,
                    modifier = Modifier.width(CHANNEL_WIDTH).height(LINE_HEIGHT),
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
            SkeletonBox(
                shape = TuripTheme.shape.container,
                modifier = Modifier.size(width = THUMBNAIL_WIDTH, height = THUMBNAIL_HEIGHT),
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
                SkeletonBox(
                    shape = TuripTheme.shape.wideButton,
                    modifier = Modifier.width(CITY_CHIP_WIDTH).height(CITY_CHIP_HEIGHT),
                )

                SkeletonBox(
                    shape = TuripTheme.shape.container,
                    modifier = Modifier.width(DURATION_WIDTH).height(LINE_HEIGHT),
                )

                SkeletonBox(
                    shape = TuripTheme.shape.container,
                    modifier = Modifier.width(PLACE_COUNT_WIDTH).height(LINE_HEIGHT),
                )
            }
        }
    }
}

/**
 * 연관 관광지 목록의 로딩 자리. `RelatedSpotItem`(core:ui) 의 배치를 흉내 낸다.
 */
@Composable
internal fun RelatedSpotListSkeleton(modifier: Modifier = Modifier) {
    val alpha: Float = rememberSkeletonAlpha(label = "regionBriefingRelatedSpotList")

    Column(
        modifier = modifier.fillMaxWidth().graphicsLayer(alpha = alpha),
        verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.large),
    ) {
        repeat(RELATED_SPOT_SKELETON_ITEM_COUNT) {
            RelatedSpotItemSkeleton()
        }
    }
}

@Composable
private fun RelatedSpotItemSkeleton(modifier: Modifier = Modifier) {
    val itemShape = TuripTheme.shape.chip

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(color = TuripTheme.colors.container, shape = itemShape)
                .padding(TuripTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SkeletonBox(
            shape = TuripTheme.shape.container,
            modifier = Modifier.width(CATEGORY_LABEL_WIDTH).height(TITLE_HEIGHT),
        )

        Box(
            modifier =
                Modifier
                    .padding(horizontal = TuripTheme.spacing.medium)
                    .width(DIVIDER_WIDTH)
                    .height(DIVIDER_HEIGHT)
                    .background(TuripTheme.colors.border),
        )

        // 나머지 장소 자리는 실제 카드가 늘 두 줄을 차지하므로 여기서도 두 줄을 그린다.
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraSmall),
        ) {
            SkeletonBox(
                shape = TuripTheme.shape.container,
                modifier = Modifier.fillMaxWidth(HEADLINE_SPOT_WIDTH_RATIO).height(TITLE_HEIGHT),
            )

            SkeletonBox(
                shape = TuripTheme.shape.container,
                modifier = Modifier.fillMaxWidth(REMAIN_SPOTS_WIDTH_RATIO).height(LINE_HEIGHT),
            )

            SkeletonBox(
                shape = TuripTheme.shape.container,
                modifier =
                    Modifier
                        .fillMaxWidth(REMAIN_SPOTS_LAST_LINE_WIDTH_RATIO)
                        .height(LINE_HEIGHT),
            )
        }

        SkeletonBox(
            shape = itemShape,
            modifier =
                Modifier
                    .padding(start = TuripTheme.spacing.small)
                    .width(SPOT_COUNT_WIDTH)
                    .height(LINE_HEIGHT),
        )
    }
}

private const val VIDEO_SKELETON_ITEM_COUNT: Int = 3
private const val RELATED_SPOT_SKELETON_ITEM_COUNT: Int = 3
private const val TITLE_WIDTH_RATIO: Float = 0.7f
private const val HEADLINE_SPOT_WIDTH_RATIO: Float = 0.5f
private const val REMAIN_SPOTS_WIDTH_RATIO: Float = 0.8f

/** 마지막 줄은 문장이 중간에 끊긴 것처럼 보이도록 짧게 둔다. */
private const val REMAIN_SPOTS_LAST_LINE_WIDTH_RATIO: Float = 0.55f

private val CARD_BORDER_WIDTH = 1.dp
private val PROFILE_IMAGE_SIZE = 42.dp
private val THUMBNAIL_WIDTH = 160.dp
private val THUMBNAIL_HEIGHT = 90.dp
private val TITLE_HEIGHT = 18.dp
private val LINE_HEIGHT = 14.dp
private val CHANNEL_WIDTH = 140.dp
private val CITY_CHIP_WIDTH = 56.dp
private val CITY_CHIP_HEIGHT = 22.dp
private val DURATION_WIDTH = 100.dp
private val PLACE_COUNT_WIDTH = 80.dp
private val CATEGORY_LABEL_WIDTH = 46.dp
private val DIVIDER_WIDTH = 1.dp
private val DIVIDER_HEIGHT = 32.dp
private val SPOT_COUNT_WIDTH = 32.dp

@Preview(showBackground = true, name = "영상 목록 스켈레톤")
@Composable
private fun VideoListSkeletonPreview() {
    TuripTheme {
        VideoListSkeleton(modifier = Modifier.padding(TuripTheme.spacing.extraLarge))
    }
}

@Preview(showBackground = true, name = "연관 관광지 스켈레톤")
@Composable
private fun RelatedSpotListSkeletonPreview() {
    TuripTheme {
        RelatedSpotListSkeleton(modifier = Modifier.padding(TuripTheme.spacing.extraLarge))
    }
}
