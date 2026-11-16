package com.on.turip.feature.home.impl.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.component.SkeletonBox
import com.on.turip.core.designsystem.theme.TuripTheme

private val PLACEHOLDER_IMAGE_SIZE: Dp = 84.dp
private val PLACEHOLDER_LABEL_WIDTH: Dp = 40.dp
private val PLACEHOLDER_LABEL_HEIGHT: Dp = 16.dp

/**
 * [RegionItem] 과 같은 규격의 placeholder. 원형 이미지 + 지역명 한 줄.
 */
@Composable
fun RegionPlaceholderItem(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SkeletonBox(
            shape = CircleShape,
            modifier =
                Modifier
                    .padding(TuripTheme.spacing.small)
                    .size(PLACEHOLDER_IMAGE_SIZE),
        )
        SkeletonBox(
            shape = TuripTheme.shape.container,
            modifier =
                Modifier
                    .padding(vertical = TuripTheme.spacing.extraSmall)
                    .width(PLACEHOLDER_LABEL_WIDTH)
                    .height(PLACEHOLDER_LABEL_HEIGHT),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegionPlaceholderItemPreview() {
    TuripTheme {
        RegionPlaceholderItem()
    }
}
