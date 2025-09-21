package com.on.turip.ui.compose.common

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.on.turip.R

@Composable
fun CircularImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    AsyncImage(
        model =
            ImageRequest
                .Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier =
            modifier
                .aspectRatio(1f)
                .clip(CircleShape),
        error = painterResource(R.drawable.ic_sorry),
    )
}
