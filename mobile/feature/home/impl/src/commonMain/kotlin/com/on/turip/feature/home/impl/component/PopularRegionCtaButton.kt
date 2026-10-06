package com.on.turip.feature.home.impl.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
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
import androidx.compose.foundation.layout.height
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
 * Top 10 지역을 [ROLL_INTERVAL_MILLIS] 간격으로 한 곳씩 바꿔 보여 준다.
 *
 * 카드를 누르면 그 순간 보이던 지역이 선택된 채로 지도가 열리고([onDestinationClick]),
 * 헤더의 `전체보기` 를 누르면 아무 지역도 고르지 않은 지도가 열린다([onMoreClick]).
 *
 * [destinations] 가 비어 있으면(로딩 중이거나 조회 실패) 같은 크기의 스켈레톤 카드를 그린다.
 * 인기 관광지 조회는 홈의 필수 데이터가 아니라서, 실패해도 이 카드는 그대로 지도로 갈 수 있어야 한다.
 * 이때 카드를 누르면 고를 지역이 없으므로 `전체보기` 와 같이 움직인다.
 */
@Composable
fun PopularRegionCtaButton(
    destinations: List<PopularDestinationModel>,
    onDestinationClick: (regionCategoryName: String) -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // 도는 위치는 화면에만 있는 값이라 ViewModel 이 아니라 여기서 센다. 화면을 벗어나면 코루틴이 함께 멈춘다.
    var index: Int by remember(destinations) { mutableIntStateOf(0) }

    LaunchedEffect(destinations) {
        if (destinations.isEmpty()) return@LaunchedEffect
        while (true) {
            delay(ROLL_INTERVAL_MILLIS)
            index = (index + 1) % destinations.size
        }
    }

    // 카드가 넘어가는 도중에는 아직 이전 카드가 보이는 중이라 그 카드를 누른 것으로 친다.
    // currentState 는 전환이 끝나야 index 를 따라잡는다.
    val transition: Transition<Int> = updateTransition(targetState = index, label = "popularDestinationRoll")

    // 애니메이션이 도는 도중 목록이 짧아질 수 있어 방어한다.
    val currentDestination: PopularDestinationModel? =
        destinations.getOrNull(transition.currentState) ?: destinations.firstOrNull()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
    ) {
        PopularRegionSectionHeader(onMoreClick = onMoreClick)

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(CARD_ASPECT_RATIO)
                    // clickable 앞에서 잘라야 리플이 카드의 둥근 모서리를 따른다.
                    .clip(TuripTheme.shape.largeContainer)
                    .background(TuripTheme.colors.cardBackground)
                    .clickable {
                        if (currentDestination != null) {
                            onDestinationClick(currentDestination.regionCategoryName)
                        } else {
                            onMoreClick()
                        }
                    },
        ) {
            if (currentDestination != null) {
                RollingDestinationCard(
                    transition = transition,
                    destinations = destinations,
                )
            } else {
                DestinationPlaceholderCard()
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
 * 전환 중에는 빠져나가는 카드와 들어오는 카드가 함께 그려진다. 각 카드는 자기 칸(람다가 받은 `targetIndex`)의
 * 지역을 그려야 빠져나가는 카드가 다음 지역으로 바뀐 채 밀려나지 않는다.
 */
@Composable
private fun RollingDestinationCard(
    transition: Transition<Int>,
    destinations: List<PopularDestinationModel>,
    modifier: Modifier = Modifier,
) {
    transition.AnimatedContent(
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

/** 실제 카드와 같은 자리에 순위 뱃지·지역명·방문자 줄 모양을 깔아 둔다. */
@Composable
private fun DestinationPlaceholderCard(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(TuripTheme.colors.gray01),
    ) {
        Box(
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .padding(TuripTheme.spacing.large)
                    .size(RANK_BADGE_SIZE)
                    .background(TuripTheme.colors.white, CircleShape),
        )

        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(TuripTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(PLACEHOLDER_TITLE_WIDTH_RATIO)
                        .height(PLACEHOLDER_TITLE_HEIGHT)
                        .background(TuripTheme.colors.white, TuripTheme.shape.container),
            )
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(PLACEHOLDER_LINE_WIDTH_RATIO)
                        .height(PLACEHOLDER_LINE_HEIGHT)
                        .background(TuripTheme.colors.white, TuripTheme.shape.container),
            )
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
private const val PLACEHOLDER_TITLE_WIDTH_RATIO: Float = 0.3f
private const val PLACEHOLDER_LINE_WIDTH_RATIO: Float = 0.5f
private val PLACEHOLDER_TITLE_HEIGHT: Dp = 28.dp
private val PLACEHOLDER_LINE_HEIGHT: Dp = 14.dp

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
            onDestinationClick = {},
            onMoreClick = {},
        )
    }
}

@Preview(showBackground = true, name = "인기 관광지 없음")
@Composable
private fun PopularRegionCtaButtonEmptyPreview() {
    TuripTheme {
        PopularRegionCtaButton(destinations = emptyList(), onDestinationClick = {}, onMoreClick = {})
    }
}
