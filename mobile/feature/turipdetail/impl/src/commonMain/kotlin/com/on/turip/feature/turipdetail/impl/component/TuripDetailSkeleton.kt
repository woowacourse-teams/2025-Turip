package com.on.turip.feature.turipdetail.impl.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.component.SkeletonBox
import com.on.turip.core.designsystem.component.rememberSkeletonAlpha
import com.on.turip.core.designsystem.theme.TuripTheme

private const val TURIP_DETAIL_SKELETON_PLACE_ITEM_COUNT = 4

@Composable
fun TuripDetailSkeleton(modifier: Modifier = Modifier) {
    val alpha = rememberSkeletonAlpha(label = "turipDetailSkeleton")

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .systemBarsPadding()
                .graphicsLayer(alpha = alpha),
    ) {
        HeaderSkeleton()

        InfoRowSkeleton(
            modifier = Modifier.padding(vertical = TuripTheme.spacing.small),
        )

        Spacer(modifier = Modifier.size(TuripTheme.spacing.extraSmall))

        MapSkeleton()

        PlacesSkeleton(
            modifier =
                Modifier
                    .padding(horizontal = TuripTheme.spacing.large)
                    .padding(vertical = TuripTheme.spacing.medium),
        )
    }
}

@Composable
private fun HeaderSkeleton(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(
                    horizontal = TuripTheme.spacing.medium,
                    vertical = TuripTheme.spacing.small,
                ),
    ) {
        SkeletonBox(
            shape = TuripTheme.shape.chip,
            modifier = Modifier.size(40.dp),
        )

        Spacer(modifier = Modifier.weight(1f))

        SkeletonBox(
            shape = TuripTheme.shape.container,
            modifier = Modifier.width(120.dp).height(24.dp),
        )

        Spacer(modifier = Modifier.weight(1f))

        SkeletonBox(
            shape = TuripTheme.shape.chip,
            modifier = Modifier.size(40.dp),
        )
    }
}

@Composable
private fun InfoRowSkeleton(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = TuripTheme.spacing.large),
    ) {
        SkeletonBox(
            shape = TuripTheme.shape.container,
            modifier = Modifier.width(88.dp).height(18.dp),
        )

        SkeletonBox(
            shape = TuripTheme.shape.container,
            modifier = Modifier.width(88.dp).height(18.dp),
        )
    }
}

@Composable
private fun MapSkeleton(modifier: Modifier = Modifier) {
    SkeletonBox(
        shape = TuripTheme.shape.container,
        modifier =
            modifier
                .fillMaxWidth()
                .height(260.dp),
    )
}

@Composable
private fun PlacesSkeleton(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
        modifier = modifier.fillMaxWidth(),
    ) {
        repeat(TURIP_DETAIL_SKELETON_PLACE_ITEM_COUNT) {
            SkeletonBox(
                shape = TuripTheme.shape.container,
                modifier = Modifier.fillMaxWidth().height(84.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TuripDetailSkeletonPreview() {
    TuripTheme {
        TuripDetailSkeleton()
    }
}
