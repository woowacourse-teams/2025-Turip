package com.on.turip.ui.compose.main.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.domain.region.RegionCategory

@Composable
fun RegionList(
    regions: List<RegionCategory>,
    onRegionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier =
            modifier
                .wrapContentHeight()
                .background(colorResource(R.color.pure_white_ffffff)),
        contentPadding = PaddingValues(start = 20.dp, top = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(regions, key = { it.name }) { regionCategory: RegionCategory ->
            RegionItem(
                region = regionCategory,
                modifier = Modifier.clickable { onRegionClick(regionCategory.name) },
            )
        }
    }
}
