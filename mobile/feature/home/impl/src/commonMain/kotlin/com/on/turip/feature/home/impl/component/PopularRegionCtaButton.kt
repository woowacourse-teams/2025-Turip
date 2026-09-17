package com.on.turip.feature.home.impl.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.home_popular_region_cta
import com.on.turip.core.designsystem.generated.resources.home_popular_region_cta_map
import com.on.turip.core.designsystem.generated.resources.home_popular_region_cta_map_description
import com.on.turip.core.designsystem.generated.resources.home_popular_region_cta_visitor
import com.on.turip.core.designsystem.generated.resources.ic_location
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.util.formatResource
import com.on.turip.feature.home.impl.model.PopularDestinationModel
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * 인기 관광지 지도로 가는 진입 카드.
 *
 * 섹션 헤더(제목 + `전체보기`) 아래에 지역 사진을 배경으로 한 카드를 두고,
 * Top 10 지역을 [ROLL_INTERVAL_MILLIS] 간격으로 한 곳씩 바꿔 보여 준다. 헤더의 `전체보기` 와 카드 어디를 눌러도 지도로 간다.
 *
 * [destinations] 가 비어 있으면(로딩 중이거나 조회 실패) 같은 크기의 빈 카드만 그린다.
 * 인기 관광지 조회는 홈의 필수 데이터가 아니라서, 실패해도 이 카드는 그대로 지도로 갈 수 있어야 한다.
 */
@Composable
fun PopularRegionCtaButton(
    destinations: List<PopularDestinationModel>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
    ) {
        PopularRegionSectionHeader(onMoreClick = onClick)

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(CARD_ASPECT_RATIO)
                    // clickable 앞에서 잘라야 리플이 카드의 둥근 모서리를 따른다.
                    .clip(TuripTheme.shape.largeContainer)
                    .background(TuripTheme.colors.cardBackground)
                    .clickable(onClick = onClick),
        ) {
            if (destinations.isNotEmpty()) {
                RollingDestinationCard(destinations = destinations)
            }
        }
    }
}

@Composable
private fun PopularRegionSectionHeader(
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.home_popular_region_cta),
            color = TuripTheme.colors.gray04,
            style = TuripTheme.typography.title1,
            modifier = Modifier.weight(1f),
        )

        Row(
            modifier =
                Modifier
                    .clip(TuripTheme.shape.container)
                    .clickable(onClick = onMoreClick)
                    .padding(
                        horizontal = TuripTheme.spacing.extraSmall,
                        vertical = TuripTheme.spacing.extraSmall,
                    ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.home_popular_region_cta_map),
                color = TuripTheme.colors.primary,
                style = TuripTheme.typography.info1,
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(Res.string.home_popular_region_cta_map_description),
                tint = TuripTheme.colors.primary,
                modifier = Modifier.size(MORE_ICON_SIZE),
            )
        }
    }
}

/**
 * 현재 카드가 왼쪽으로 빠지고 다음 카드가 오른쪽에서 밀려 들어오는 페이저 전환.
 *
 * 도는 위치는 화면에만 있는 값이라 ViewModel 이 아니라 여기서 센다. 화면을 벗어나면 코루틴이 함께 멈춘다.
 */
@Composable
private fun RollingDestinationCard(
    destinations: List<PopularDestinationModel>,
    modifier: Modifier = Modifier,
) {
    var index: Int by remember(destinations) { mutableIntStateOf(0) }

    LaunchedEffect(destinations) {
        while (true) {
            delay(ROLL_INTERVAL_MILLIS)
            index = (index + 1) % destinations.size
        }
    }

    AnimatedContent(
        targetState = index,
        transitionSpec = {
            slideInHorizontally(animationSpec = tween(SLIDE_DURATION_MILLIS)) { width: Int -> width }
                .togetherWith(
                    slideOutHorizontally(animationSpec = tween(SLIDE_DURATION_MILLIS)) { width: Int -> -width },
                )
        },
        modifier =
            modifier
                .fillMaxSize()
                // 카드 밖으로 밀려나는 이전 장이 둥근 모서리 바깥에 비치지 않도록 잘라 낸다.
                .clipToBounds(),
        label = "popularDestinationRoll",
    ) { targetIndex: Int ->
        // 애니메이션이 도는 도중 목록이 짧아질 수 있어 방어한다.
        val destination: PopularDestinationModel =
            destinations.getOrNull(targetIndex) ?: destinations.first()
        DestinationCard(destination = destination)
    }
}

@Composable
private fun DestinationCard(
    destination: PopularDestinationModel,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (destination.imageUrl != null) {
            AsyncImage(
                model = destination.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // 사진이 밝아도 흰 글씨가 읽히도록 아래쪽을 어둡게 깐다.
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            SCRIM_START_FRACTION to Color.Transparent,
                            1f to TuripTheme.colors.scrim,
                        ),
                    ),
        )

        RankBadge(
            rank = destination.rank,
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .padding(TuripTheme.spacing.large),
        )

        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(TuripTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraSmall),
        ) {
            Text(
                text = destination.regionCategoryName,
                color = TuripTheme.colors.white,
                style = TuripTheme.typography.display,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraSmall),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_location),
                    contentDescription = null,
                    tint = TuripTheme.colors.white,
                    modifier = Modifier.size(LOCATION_ICON_SIZE),
                )
                Text(
                    text =
                        stringResource(Res.string.home_popular_region_cta_visitor)
                            .formatResource(destination.visitorCountText),
                    color = TuripTheme.colors.white,
                    style = TuripTheme.typography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun RankBadge(
    rank: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(RANK_BADGE_SIZE)
                .background(color = TuripTheme.colors.white, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = rank.toString(),
            color = TuripTheme.colors.gray04,
            style = TuripTheme.typography.title2,
        )
    }
}

private const val ROLL_INTERVAL_MILLIS: Long = 3_000L
private const val SLIDE_DURATION_MILLIS: Int = 1000
private const val CARD_ASPECT_RATIO: Float = 4f / 3f
private const val SCRIM_START_FRACTION: Float = 0.45f
private val RANK_BADGE_SIZE: Dp = 36.dp
private val MORE_ICON_SIZE: Dp = 18.dp
private val LOCATION_ICON_SIZE: Dp = 14.dp

@Preview(showBackground = true)
@Composable
private fun PopularRegionCtaButtonPreview() {
    TuripTheme {
        PopularRegionCtaButton(
            destinations =
                listOf(
                    PopularDestinationModel(rank = 1, regionCategoryName = "서울", imageUrl = null, visitorCountText = "2847만"),
                    PopularDestinationModel(rank = 2, regionCategoryName = "부산", imageUrl = null, visitorCountText = "1204만"),
                ),
            onClick = {},
        )
    }
}

@Preview(showBackground = true, name = "인기 관광지 없음")
@Composable
private fun PopularRegionCtaButtonEmptyPreview() {
    TuripTheme {
        PopularRegionCtaButton(destinations = emptyList(), onClick = {})
    }
}
