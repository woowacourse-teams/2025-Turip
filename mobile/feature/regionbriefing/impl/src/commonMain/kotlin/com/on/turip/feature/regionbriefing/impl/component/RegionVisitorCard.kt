package com.on.turip.feature.regionbriefing.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.region_briefing_visitor_label
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.util.formatResource
import org.jetbrains.compose.resources.stringResource

/**
 * 브리핑 맨 위의 방문자 수 카드. 지도 시트의 `RegionStatCard` 와 같은 톤이다.
 *
 * 방문자 수는 기준월이 없으면 무엇과 견줄 값인지 알 수 없으므로 라벨에 기준월을 함께 적는다.
 */
@Composable
internal fun RegionVisitorCard(
    baseMonthText: String,
    visitorCountText: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = TuripTheme.colors.container,
                    shape = TuripTheme.shape.largeContainer,
                ).padding(TuripTheme.spacing.large),
    ) {
        Text(
            text =
                stringResource(Res.string.region_briefing_visitor_label)
                    .formatResource(baseMonthText),
            style = TuripTheme.typography.info1,
            color = TuripTheme.colors.gray03,
        )
        Text(
            text = visitorCountText,
            style = TuripTheme.typography.title1,
            color = TuripTheme.colors.black,
            modifier = Modifier.padding(top = TuripTheme.spacing.extraSmall),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegionVisitorCardPreview() {
    TuripTheme {
        RegionVisitorCard(
            baseMonthText = "2025년 6월",
            visitorCountText = "480만",
        )
    }
}
