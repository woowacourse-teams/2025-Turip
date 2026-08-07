package com.on.turip.feature.home.impl.component

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.bg_image_placeholder
import com.on.turip.core.designsystem.generated.resources.ic_sorry
import org.jetbrains.compose.resources.painterResource

@Composable
fun CircularImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier =
            modifier
                .aspectRatio(1f)
                .clip(CircleShape),
        placeholder = painterResource(Res.drawable.bg_image_placeholder),
        error = painterResource(Res.drawable.ic_sorry),
    )
}
