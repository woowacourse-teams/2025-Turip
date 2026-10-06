package com.on.turip.feature.home.impl.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.component.SkeletonBox
import com.on.turip.core.designsystem.theme.TuripTheme

private val PLACEHOLDER_THUMBNAIL_HEIGHT: Dp = 140.dp
private val PLACEHOLDER_CHIP_HEIGHT: Dp = 24.dp
private val PLACEHOLDER_LINE_HEIGHT: Dp = 14.dp
private const val PLACEHOLDER_CHIP_WIDTH_RATIO = 0.2f
private const val PLACEHOLDER_TITLE_WIDTH_RATIO = 0.9f
private const val PLACEHOLDER_SUB_WIDTH_RATIO = 0.5f

/**
 * [UsersLikeItem] 과 같은 규격의 placeholder. 응답이 오기 전 섹션 높이가 흔들리지 않게 한다.
 */
@Composable
fun UsersLikePlaceholderItem(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .border(
                    width = 1.dp,
                    color = TuripTheme.colors.gray02,
                    shape = TuripTheme.shape.chip,
                ).padding(TuripTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraSmall),
    ) {
        SkeletonBox(
            shape = TuripTheme.shape.container,
            modifier = Modifier.fillMaxWidth().height(PLACEHOLDER_THUMBNAIL_HEIGHT),
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.medium))

        SkeletonBox(
            shape = TuripTheme.shape.chip,
            modifier = Modifier.fillMaxWidth(PLACEHOLDER_CHIP_WIDTH_RATIO).height(PLACEHOLDER_CHIP_HEIGHT),
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.extraSmall))

        SkeletonBox(
            shape = TuripTheme.shape.container,
            modifier = Modifier.fillMaxWidth(PLACEHOLDER_TITLE_WIDTH_RATIO).height(PLACEHOLDER_LINE_HEIGHT),
        )
        SkeletonBox(
            shape = TuripTheme.shape.container,
            modifier = Modifier.fillMaxWidth(PLACEHOLDER_SUB_WIDTH_RATIO).height(PLACEHOLDER_LINE_HEIGHT),
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.extraSmall))

        SkeletonBox(
            shape = TuripTheme.shape.container,
            modifier = Modifier.fillMaxWidth(PLACEHOLDER_SUB_WIDTH_RATIO).height(PLACEHOLDER_LINE_HEIGHT),
        )
        SkeletonBox(
            shape = TuripTheme.shape.container,
            modifier = Modifier.fillMaxWidth(PLACEHOLDER_SUB_WIDTH_RATIO).height(PLACEHOLDER_LINE_HEIGHT),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UsersLikePlaceholderItemPreview() {
    TuripTheme {
        UsersLikePlaceholderItem(modifier = Modifier.width(280.dp))
    }
}
