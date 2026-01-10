package com.on.turip.ui.compose.trip.component

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.on.turip.R

@Composable
fun CreatorThumbnail(
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
                .size(84.dp)
                .aspectRatio(1f)
                .clip(CircleShape),
        placeholder = painterResource(R.drawable.bg_image_placeholder),
        error = painterResource(R.drawable.ic_sorry),
    )
}
