package com.on.turip.feature.mypage.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.component.SkeletonBox
import com.on.turip.core.designsystem.component.rememberSkeletonAlpha
import com.on.turip.core.designsystem.theme.TuripTheme

@Composable
fun MyPageBookmarkContentItemSkeleton(modifier: Modifier = Modifier) {
    val alpha = rememberSkeletonAlpha(label = "myPageBookmarkContentItemSkeleton")

    Column(
        modifier =
            modifier
                .border(1.dp, TuripTheme.colors.border, TuripTheme.shape.container)
                .clip(TuripTheme.shape.container)
                .background(TuripTheme.colors.white)
                .padding(TuripTheme.spacing.extraSmall)
                .graphicsLayer(alpha = alpha),
    ) {
        SkeletonBox(
            shape = TuripTheme.shape.container,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.medium))

        SkeletonBox(
            shape = TuripTheme.shape.container,
            modifier = Modifier.fillMaxWidth(0.85f).height(18.dp),
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.small))

        SkeletonBox(
            shape = TuripTheme.shape.container,
            modifier = Modifier.width(160.dp).height(14.dp),
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.small))

        Row(
            horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
        ) {
            SkeletonBox(
                shape = TuripTheme.shape.container,
                modifier = Modifier.width(72.dp).height(14.dp),
            )

            SkeletonBox(
                shape = TuripTheme.shape.container,
                modifier = Modifier.width(72.dp).height(14.dp),
            )
        }

        Spacer(modifier = Modifier.height(TuripTheme.spacing.small))
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageBookmarkContentItemSkeletonPreview() {
    TuripTheme {
        MyPageBookmarkContentItemSkeleton(
            modifier =
                Modifier
                    .width(280.dp)
                    .padding(TuripTheme.spacing.large),
        )
    }
}
