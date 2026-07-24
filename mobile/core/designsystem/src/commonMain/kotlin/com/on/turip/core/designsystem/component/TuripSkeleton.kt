package com.on.turip.core.designsystem.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import com.on.turip.core.designsystem.theme.TuripTheme

private const val SKELETON_ALPHA_DURATION_MILLIS = 700
private const val SKELETON_ALPHA_INITIAL = 0.4f
private const val SKELETON_ALPHA_TARGET = 1f

@Composable
fun rememberSkeletonAlpha(label: String = "skeleton"): Float {
    val transition = rememberInfiniteTransition(label = label)
    val alpha by transition.animateFloat(
        initialValue = SKELETON_ALPHA_INITIAL,
        targetValue = SKELETON_ALPHA_TARGET,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = SKELETON_ALPHA_DURATION_MILLIS, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "${label}Alpha",
    )
    return alpha
}

@Composable
fun SkeletonBox(
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .clip(shape)
                .background(TuripTheme.colors.gray01),
    )
}
