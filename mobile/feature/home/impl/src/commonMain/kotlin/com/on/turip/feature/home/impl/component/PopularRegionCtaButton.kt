package com.on.turip.feature.home.impl.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.home_popular_region_cta
import com.on.turip.core.designsystem.theme.TuripTheme
import org.jetbrains.compose.resources.stringResource

/**
 * 인기 관광지 지도로 가는 진입 버튼.
 *
 * 바로 위의 [RandomTravelCtaButton] 과 나란히 서기 때문에, 주 CTA 자리를 뺏지 않도록
 * 채운 버튼이 아니라 외곽선 버튼으로 둔다.
 */
@Composable
fun PopularRegionCtaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        shape = TuripTheme.shape.wideButton,
        colors =
            ButtonDefaults.outlinedButtonColors(
                containerColor = TuripTheme.colors.white,
                contentColor = TuripTheme.colors.primary,
            ),
        modifier =
            modifier
                .fillMaxWidth()
                .height(CTA_HEIGHT),
    ) {
        Text(
            text = stringResource(Res.string.home_popular_region_cta),
            style = TuripTheme.typography.title2,
        )
    }
}

private val CTA_HEIGHT = 52.dp

@Preview(showBackground = true)
@Composable
private fun PopularRegionCtaButtonPreview() {
    TuripTheme {
        PopularRegionCtaButton(onClick = {})
    }
}
