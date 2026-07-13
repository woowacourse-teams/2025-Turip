package com.on.turip.feature.turip.impl.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.theme.TuripTheme

@Composable
fun MyTuripCardSkeleton(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "myTuripCardSkeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 700, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "myTuripCardSkeletonAlpha",
    )

    Card(
        shape = TuripTheme.shape.largeContainer,
        colors = CardDefaults.cardColors(containerColor = TuripTheme.colors.container),
        border = BorderStroke(width = 1.dp, color = TuripTheme.colors.border),
        modifier = modifier.fillMaxWidth().graphicsLayer(alpha = alpha),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.large),
            modifier = Modifier.padding(TuripTheme.spacing.large),
        ) {
            SkeletonBox(
                shape = TuripTheme.shape.chip,
                modifier = Modifier.size(88.dp),
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
            ) {
                SkeletonBox(
                    shape = TuripTheme.shape.container,
                    modifier = Modifier.width(52.dp).height(20.dp),
                )

                SkeletonBox(
                    shape = TuripTheme.shape.container,
                    modifier = Modifier.fillMaxWidth(0.6f).height(20.dp),
                )

                SkeletonBox(
                    shape = TuripTheme.shape.container,
                    modifier = Modifier.width(64.dp).height(16.dp),
                )
            }
        }
    }
}

@Composable
private fun SkeletonBox(
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clip(shape)
                .background(TuripTheme.colors.gray01),
    ) {}
}

@Preview(showBackground = true)
@Composable
private fun MyTuripCardSkeletonPreview() {
    TuripTheme {
        Column(verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium)) {
            MyTuripCardSkeleton()
            MyTuripCardSkeleton()
            MyTuripCardSkeleton()
        }
    }
}
