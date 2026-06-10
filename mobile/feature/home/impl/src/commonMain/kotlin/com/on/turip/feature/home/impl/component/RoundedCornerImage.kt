package com.on.turip.feature.home.impl.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun RoundedCornerImage(
    imageUrl: String,
    cornerRadiusDp: Dp = 12.dp,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null,
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier =
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadiusDp)),
    )
}
