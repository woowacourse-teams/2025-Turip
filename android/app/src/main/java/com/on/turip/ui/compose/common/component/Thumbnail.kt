package com.on.turip.ui.compose.common.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Thumbnail(
    imageUrl: String,
    modifier: Modifier = Modifier,
    cornerRadiusDp: Dp = 12.dp,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null,
    chip: @Composable () -> Unit = {},
) {
    Box(modifier = modifier) {
        RoundedCornerImage(
            imageUrl = imageUrl,
            cornerRadiusDp = cornerRadiusDp,
            contentScale = contentScale,
            contentDescription = contentDescription,
        )
        chip()
    }
}
