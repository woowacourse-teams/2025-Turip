package com.on.turip.feature.mypage.impl.component

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.bg_image_placeholder
import com.on.turip.core.designsystem.theme.TuripTheme
import org.jetbrains.compose.resources.painterResource

@Composable
fun ContentThumbnail(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val shape = TuripTheme.shape.container
    val parsedUrl = convertVideoThumbnailUrl(imageUrl)

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
            model = parsedUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
            error = painterResource(Res.drawable.bg_image_placeholder),
        )
    }
}

private fun convertVideoThumbnailUrl(url: String): String =
    when {
        url.contains("img.youtube.com") -> url.replace("default.jpg", "hqdefault.jpg")
        else -> url
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
