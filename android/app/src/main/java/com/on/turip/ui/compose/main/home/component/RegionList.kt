package com.on.turip.ui.compose.main.home.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.on.turip.domain.region.RegionCategory

@Composable
fun RegionList(
    regions: List<RegionCategory>,
    onRegionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        maxItemsInEachRow = 4,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        regions.forEach { regionCategory: RegionCategory ->
            RegionItem(
                region = regionCategory,
                modifier = Modifier.clickable { onRegionClick(regionCategory.name) },
            )
        }
    }
}
