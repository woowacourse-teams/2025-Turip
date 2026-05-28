package com.on.turip.feature.home.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.on.turip.core.designsystem.theme.TuripTheme

@Composable
fun UsersLikeItem(
    thumbnailUrl: String?,
    title: String,
    channelName: String,
    tripDuration: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .border(width = 1.dp, color = TuripTheme.colors.gray02, shape = TuripTheme.shape.chip)
            .padding(TuripTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraSmall),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(TuripTheme.shape.container)
                .background(TuripTheme.colors.gray01),
        ) {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
        }

        Spacer(modifier = Modifier.height(TuripTheme.spacing.medium))

        Text(
            text = title,
            style = TuripTheme.typography.body2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.extraSmall))

        Text(
            text = channelName,
            color = TuripTheme.colors.gray03,
            style = TuripTheme.typography.info2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = tripDuration,
            color = TuripTheme.colors.gray03,
            style = TuripTheme.typography.info2,
        )
    }
}
