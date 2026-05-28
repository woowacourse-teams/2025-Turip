package com.on.turip.feature.home.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.model.RegionCategory

@Composable
fun RegionItem(
    region: RegionCategory,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .padding(TuripTheme.spacing.small)
                .size(84.dp)
                .clip(CircleShape)
                .background(TuripTheme.colors.gray01),
        ) {
            AsyncImage(
                model = region.imageUrl,
                contentDescription = region.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
        }
        Text(
            text = region.name,
            style = TuripTheme.typography.title3,
            modifier = Modifier.padding(vertical = TuripTheme.spacing.extraSmall),
        )
    }
}
