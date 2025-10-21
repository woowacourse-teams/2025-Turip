package com.on.turip.ui.compose.main.home.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.on.turip.domain.region.RegionCategory

private const val MAX_REGION_COUNT_IN_EACH_ROW = 4

@Composable
fun RegionList(
    regions: List<RegionCategory>,
    onRegionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rowCount = (regions.lastIndex / MAX_REGION_COUNT_IN_EACH_ROW) + 1
    val height = rowCount.times(128.dp)
    LazyVerticalGrid(
        columns = GridCells.Fixed(count = MAX_REGION_COUNT_IN_EACH_ROW),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier =
            modifier
                .height(height)
                .fillMaxWidth(),
    ) {
        items(regions, key = { it.name }) { regionCategory: RegionCategory ->
            RegionItem(
                region = regionCategory,
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onRegionClick(regionCategory.name) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegionListPreview() {
    RegionList(
        regions =
            listOf(
                RegionCategory("서울", "", null),
                RegionCategory("부산", "", null),
                RegionCategory("대전", "", null),
                RegionCategory("대구", "", null),
                RegionCategory("울산", "", null),
                RegionCategory("포항", "", null),
                RegionCategory("강릉", "", null),
                RegionCategory("여수", "", null),
                RegionCategory("기타", "", null),
            ),
        onRegionClick = {},
    )
}
