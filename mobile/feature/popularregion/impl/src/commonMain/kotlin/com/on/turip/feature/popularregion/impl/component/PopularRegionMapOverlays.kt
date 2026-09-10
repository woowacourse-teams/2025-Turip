package com.on.turip.feature.popularregion.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.popular_region_legend_high
import com.on.turip.core.designsystem.generated.resources.popular_region_legend_low
import com.on.turip.core.designsystem.generated.resources.popular_region_legend_title
import com.on.turip.core.designsystem.generated.resources.popular_region_map_badge
import com.on.turip.core.designsystem.generated.resources.popular_region_map_hint
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.util.formatResource
import org.jetbrains.compose.resources.stringResource

/** 지도 좌상단 배지 — 지금 지도가 무엇을 기준으로 칠해져 있는지 알려준다. */
@Composable
internal fun PopularRegionMapBadge(
    baseMonthText: String,
    modifier: Modifier = Modifier,
) {
    OverlayCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.BarChart,
                contentDescription = null,
                tint = PopularRegionMapPalette.HeatHigh,
                modifier = Modifier.size(BADGE_ICON_SIZE),
            )
            Text(
                text =
                    stringResource(Res.string.popular_region_map_badge)
                        .formatResource(baseMonthText),
                style = TuripTheme.typography.info1,
                color = TuripTheme.colors.gray04,
                modifier = Modifier.padding(start = TuripTheme.spacing.small),
            )
        }
    }
}

/** 지도 우상단 범례 — 색 농도가 방문자 수라는 것을 밝힌다. */
@Composable
internal fun HeatLegendCard(modifier: Modifier = Modifier) {
    OverlayCard(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(Res.string.popular_region_legend_title),
                style = TuripTheme.typography.info2,
                color = TuripTheme.colors.gray04,
            )
            Row(
                modifier = Modifier.padding(top = TuripTheme.spacing.small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier =
                        Modifier
                            .width(LEGEND_BAR_WIDTH)
                            .height(LEGEND_BAR_HEIGHT)
                            .background(
                                brush =
                                    Brush.verticalGradient(
                                        listOf(
                                            PopularRegionMapPalette.HeatHigh,
                                            PopularRegionMapPalette.HeatLow,
                                        ),
                                    ),
                                shape = TuripTheme.shape.wideButton,
                            ),
                ) {}
                Column(
                    modifier =
                        Modifier
                            .padding(start = TuripTheme.spacing.small)
                            .height(LEGEND_BAR_HEIGHT),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(Res.string.popular_region_legend_high),
                        style = TuripTheme.typography.info2,
                        color = TuripTheme.colors.gray03,
                    )
                    Text(
                        text = stringResource(Res.string.popular_region_legend_low),
                        style = TuripTheme.typography.info2,
                        color = TuripTheme.colors.gray03,
                    )
                }
            }
        }
    }
}

/** 아직 아무 지역도 고르지 않았을 때 지도 하단에 뜨는 안내. */
@Composable
internal fun PopularRegionMapHint(modifier: Modifier = Modifier) {
    OverlayCard(modifier = modifier) {
        Text(
            text = stringResource(Res.string.popular_region_map_hint),
            style = TuripTheme.typography.info1,
            color = TuripTheme.colors.gray03,
        )
    }
}

@Composable
private fun OverlayCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier =
            modifier
                .background(
                    color = TuripTheme.colors.white,
                    shape = TuripTheme.shape.largeContainer,
                ).padding(
                    horizontal = TuripTheme.spacing.medium,
                    vertical = TuripTheme.spacing.small,
                ),
        content = { content() },
    )
}

private val BADGE_ICON_SIZE = 16.dp
private val LEGEND_BAR_WIDTH = 10.dp
private val LEGEND_BAR_HEIGHT = 90.dp

@Preview(showBackground = true)
@Composable
private fun PopularRegionMapOverlaysPreview() {
    TuripTheme {
        Column(verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small)) {
            PopularRegionMapBadge(baseMonthText = "2025년 6월")
            HeatLegendCard()
            PopularRegionMapHint()
        }
    }
}
