package com.on.turip.feature.home.impl.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.model.RegionCategory

private const val MAX_COLUMNS = 4

@Composable
fun RegionList(
    regions: List<RegionCategory>,
    onRegionClick: (regionName: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val distinctRegions = regions.distinctBy { it.name }
    val rowCount = (distinctRegions.lastIndex / MAX_COLUMNS) + 1
    LazyVerticalGrid(
        columns = GridCells.Fixed(MAX_COLUMNS),
        verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
        modifier = modifier
            .height(rowCount.times(128.dp))
            .fillMaxWidth(),
    ) {
        items(distinctRegions, key = { it.name }) { region ->
            RegionItem(
                region = region,
                modifier = Modifier
                    .clip(TuripTheme.shape.chip)
                    .clickable { onRegionClick(region.name) },
            )
        }
    }
}
