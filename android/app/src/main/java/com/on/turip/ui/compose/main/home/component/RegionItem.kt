package com.on.turip.ui.compose.main.home.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.on.turip.domain.region.RegionCategory
import com.on.turip.ui.compose.common.component.CircularImage
import com.on.turip.ui.compose.theme.TuripTypography

@Composable
fun RegionItem(
    region: RegionCategory,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularImage(
            imageUrl = region.imageUrl,
            modifier =
                Modifier.size(84.dp),
        )
        Text(
            text = region.name,
            style = TuripTypography.titleSmall,
            modifier = Modifier.padding(vertical = 4.dp),
        )
    }
}
