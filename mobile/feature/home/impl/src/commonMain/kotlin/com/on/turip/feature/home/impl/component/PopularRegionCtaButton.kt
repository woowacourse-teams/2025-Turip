package com.on.turip.feature.home.impl.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.home_popular_region_cta
import com.on.turip.core.designsystem.generated.resources.home_popular_region_cta_map
import com.on.turip.core.designsystem.generated.resources.home_popular_region_cta_visitor
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.util.formatResource
import com.on.turip.feature.home.impl.model.PopularDestinationModel
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

/**
 * 인기 관광지 지도로 가는 진입 버튼.
 *
 * 아래 줄에서 Top 10 지역을 1초 간격으로 한 곳씩 돌려 보여 준다. 어디를 눌러도 지도로 간다.
 * 우측 상단의 `지도로 전체보기` 는 별도 클릭 영역이 아니라, 카드 전체가 눌린다는 것을 알리는 표시다.
 *
 * [destinations] 가 비어 있으면(로딩 중이거나 조회 실패) 아랫줄 없이 제목 줄만 그린다.
 * 인기 관광지 조회는 홈의 필수 데이터가 아니라서, 실패해도 이 버튼은 그대로 지도로 갈 수 있어야 한다.
 */
@Composable
fun PopularRegionCtaButton(
    destinations: List<PopularDestinationModel>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = TuripTheme.colors.border,
                    shape = TuripTheme.shape.wideButton,
                ).clickable(onClick = onClick)
                .padding(
                    horizontal = TuripTheme.spacing.extraLarge,
                    vertical = TuripTheme.spacing.large,
                ),
        verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.home_popular_region_cta),
                color = TuripTheme.colors.gray04,
                style = TuripTheme.typography.title2,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(Res.string.home_popular_region_cta_map),
                    color = TuripTheme.colors.primary,
                    style = TuripTheme.typography.title3,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = TuripTheme.colors.primary,
                    modifier = Modifier.size(CHEVRON_SIZE),
                )
            }
        }

        if (destinations.isNotEmpty()) {
            RollingDestination(destinations = destinations)
        }
    }
}

/**
 * 순위 한 칸이 위로 빠지고 다음 칸이 아래에서 올라오는 슬롯머신 전환.
 *
 * 도는 위치는 화면에만 있는 값이라 ViewModel 이 아니라 여기서 센다. 화면을 벗어나면 코루틴이 함께 멈춘다.
 */
@Composable
private fun RollingDestination(
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
            (slideInVertically { height: Int -> height } + fadeIn())
                .togetherWith(slideOutVertically { height: Int -> -height } + fadeOut())
        },
        modifier =
            modifier
                .fillMaxWidth()
                .height(ROLL_ROW_HEIGHT)
                .clipToBounds(),
        label = "popularDestinationRoll",
    ) { targetIndex: Int ->
        // 애니메이션이 도는 도중 목록이 짧아질 수 있어 방어한다.
        val destination: PopularDestinationModel =
            destinations.getOrNull(targetIndex) ?: destinations.first()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RankBadge(rank = destination.rank)
            Text(
                text = destination.regionCategoryName,
                color = TuripTheme.colors.gray05,
                style = TuripTheme.typography.title2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text =
                    stringResource(Res.string.home_popular_region_cta_visitor)
                        .formatResource(destination.visitorCountText),
                color = TuripTheme.colors.gray03,
                style = TuripTheme.typography.body2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
                .background(color = TuripTheme.colors.primary, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = rank.toString(),
            color = TuripTheme.colors.white,
            style = TuripTheme.typography.title3,
        )
    }
}

private const val ROLL_INTERVAL_MILLIS: Long = 3_000L
private val ROLL_ROW_HEIGHT = 24.dp
private val RANK_BADGE_SIZE = 24.dp
private val CHEVRON_SIZE = 20.dp

@Preview(showBackground = true)
@Composable
private fun PopularRegionCtaButtonPreview() {
    TuripTheme {
        PopularRegionCtaButton(
            destinations =
                listOf(
                    PopularDestinationModel(rank = 1, regionCategoryName = "서울", visitorCountText = "2847만"),
                    PopularDestinationModel(rank = 2, regionCategoryName = "부산", visitorCountText = "1204만"),
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
