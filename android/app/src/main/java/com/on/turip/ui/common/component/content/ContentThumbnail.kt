package com.on.turip.ui.common.component.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.on.turip.R
import com.on.turip.ui.common.TuripUrlConverter
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun ContentThumbnail(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val shape = TuripTheme.shape.container
    val parsedUrl = TuripUrlConverter.convertVideoThumbnailUrl(imageUrl)

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(shape)
                .border(
                    width = 1.dp,
                    color = TuripTheme.colors.gray01,
                    shape = shape,
                ),
    ) {
        AsyncImage(
            model =
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(parsedUrl)
                    .crossfade(true)
                    .build(),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
            error = painterResource(R.drawable.bg_image_placeholder),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ContentThumbnailPreview() {
    TuripTheme {
        ContentThumbnail(
            imageUrl = "",
            modifier =
                Modifier
                    .background(TuripTheme.colors.primary)
                    .padding(TuripTheme.spacing.small),
        )
    }
}
