package com.on.turip.ui.compose.common.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.transformations
import coil3.transform.RoundedCornersTransformation

@Composable
fun RoundedCornerImage(
    imageUrl: String,
    cornerRadiusDp: Dp = 12.dp,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null,
) {
    AsyncImage(
        model =
            ImageRequest
                .Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .transformations(RoundedCornersTransformation(cornerRadiusDp.value))
                .build(),
        contentDescription = contentDescription,
        contentScale = contentScale,
    )
}
