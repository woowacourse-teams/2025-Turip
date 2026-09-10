package com.on.turip.feature.popularregion.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.popularregion.impl.map.GeoPoint
import com.on.turip.feature.popularregion.impl.map.RegionShapeKey
import com.on.turip.feature.popularregion.impl.model.PopularRegionModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 방문자 수 상위 관광지를 순위대로 늘어놓은 가로 스크롤 칩 줄.
 *
 * 지도만으로는 속초·수원처럼 작은 지역을 손가락으로 정확히 누르기 어렵다.
 * 칩은 그 지역들로 가는 두 번째 길이며, 누르면 지도를 탭한 것과 **똑같은 선택**이 일어난다.
 * 선택 상태의 단일 진실 공급원은 여전히 `selectedRegionCode` 하나다.
 *
 * @param destinations 순위 오름차순. 비어 있으면 줄 자체를 그리지 않는다.
 */
@Composable
internal fun PopularDestinationChipRow(
    destinations: ImmutableList<PopularRegionModel>,
    selectedRegionCode: String?,
    onDestinationClick: (regionCode: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (destinations.isEmpty()) return

    val listState: LazyListState = rememberLazyListState()

    // 지도에서 고른 지역의 칩이 화면 밖에 있으면 선택된 티가 나지 않는다.
    // 어느 쪽으로 골랐든 그 칩이 보이도록 끌어온다.
    //
    // 이미 온전히 보이는 칩은 건드리지 않는다. 칩을 눌렀을 때마다 그게 맨 앞으로 끌려오면
    // 누른 자리가 움직여, 옆 칩을 이어서 누르기 어려워진다.
    LaunchedEffect(selectedRegionCode, destinations) {
        val selectedIndex: Int = destinations.indexOfFirst { it.code == selectedRegionCode }
        if (selectedIndex < 0) return@LaunchedEffect

        val visible: LazyListItemInfo? =
            listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == selectedIndex }
        val isFullyVisible: Boolean =
            visible != null &&
                visible.offset >= listState.layoutInfo.viewportStartOffset &&
                visible.offset + visible.size <= listState.layoutInfo.viewportEndOffset
        if (!isFullyVisible) {
            listState.animateScrollToItem(selectedIndex)
        }
    }

    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
        contentPadding =
            PaddingValues(
                horizontal = TuripTheme.spacing.large,
                vertical = TuripTheme.spacing.small,
            ),
        modifier = modifier,
    ) {
        items(
            items = destinations,
            key = { destination -> destination.code },
        ) { destination ->
            PopularDestinationChip(
                destination = destination,
                isSelected = destination.code == selectedRegionCode,
                onClick = { onDestinationClick(destination.code) },
            )
        }
    }
}

/**
 * 칩 하나. 순위와 지역명을 함께 적는다.
 *
 * 순위를 빼면 그냥 지역 목록이라 이 줄이 `Top 10` 이라는 게 화면에 드러나지 않는다.
 *
 * 선택한 칩은 테마 primary 로 채우고 나머지는 흰 바탕에 얇은 테두리만 둔다.
 * 지도의 붉은 열 색과 칩이 같은 색을 쓰면 칩이 지도 범례처럼 보여서, 칩은 앱의 선택 색을 그대로 따른다.
 *
 * 글자 스타일은 선택 여부와 관계없이 하나로 둔다. 굵기를 바꾸면 칩 너비가 달라져
 * 고를 때마다 줄 전체가 밀린다.
 */
@Composable
private fun PopularDestinationChip(
    destination: PopularRegionModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor: Color =
        if (isSelected) TuripTheme.colors.primary else TuripTheme.colors.white
    val borderColor: Color =
        if (isSelected) TuripTheme.colors.primary else TuripTheme.colors.border
    val contentColor: Color =
        if (isSelected) TuripTheme.colors.white else TuripTheme.colors.black

    Row(
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .background(color = containerColor, shape = ChipShape)
                .border(width = CHIP_BORDER_WIDTH, color = borderColor, shape = ChipShape)
                // clickable 앞에서 잘라야 리플이 알약 모양을 따른다.
                // background/border 의 shape 만으로는 리플이 사각형으로 퍼진다.
                .clip(ChipShape)
                .clickable(onClick = onClick)
                .padding(
                    horizontal = TuripTheme.spacing.extraLarge,
                    vertical = TuripTheme.spacing.medium,
                ),
    ) {
        destination.rank?.let { rank ->
            Text(
                text = rank.toString(),
                style = TuripTheme.typography.title3,
                // 순위가 지역명보다 앞서 읽히지 않도록 한 단계 흐리게 둔다.
                // 채워진 칩 위에서는 흰 글자를 그대로 쓴다. 여기서 더 흐리게 하면 잘 안 보인다.
                color = if (isSelected) contentColor else TuripTheme.colors.gray02,
            )
        }
        Text(
            text = destination.name,
            style = TuripTheme.typography.title3,
            color = contentColor,
        )
    }
}

/** 알약 모양. `TuripTheme.shape.chip`(12dp)은 이 크기에서 모서리가 각져 보인다. */
private val ChipShape = RoundedCornerShape(percent = 50)

private val CHIP_BORDER_WIDTH = 1.dp

@Preview(showBackground = true)
@Composable
private fun PopularDestinationChipRowPreview() {
    TuripTheme {
        PopularDestinationChipRow(
            destinations =
                persistentListOf(
                    PopularRegionModel(
                        shapeKey = RegionShapeKey.Destination("서울"),
                        name = "서울",
                        location = GeoPoint(37.5665, 126.9780),
                        visitorCount = 28_470_000L,
                        rank = 1,
                        regionCategoryName = "서울",
                    ),
                    PopularRegionModel(
                        shapeKey = RegionShapeKey.Destination("속초"),
                        name = "속초",
                        location = GeoPoint(38.2070, 128.5918),
                        visitorCount = 1_200_000L,
                        rank = 2,
                        regionCategoryName = "속초",
                    ),
                ),
            selectedRegionCode = "dest:속초",
            onDestinationClick = {},
        )
    }
}
