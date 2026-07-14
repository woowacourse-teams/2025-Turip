package com.on.turip.feature.home.impl.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.model.region.Country
import com.on.turip.core.model.region.RegionCategory

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
                Modifier
                    .padding(TuripTheme.spacing.small)
                    .size(84.dp),
        )
        Text(
            text = region.name,
            style = TuripTheme.typography.title3,
            modifier = Modifier.padding(vertical = TuripTheme.spacing.extraSmall),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegionItemPreview() {
    TuripTheme {
        RegionItem(
            region =
                RegionCategory(
                    name = "속초",
                    imageUrl = "",
                    country = Country(0, "", ""),
                ),
        )
    }
}
