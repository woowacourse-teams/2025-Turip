package com.on.turip.feature.popularregion.impl

import androidx.compose.runtime.Immutable
import com.on.turip.core.ui.UiState
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.util.toBaseMonthOfYear
import com.on.turip.core.ui.util.toBaseYear
import com.on.turip.feature.popularregion.impl.model.PopularRegionModel
import com.on.turip.feature.popularregion.impl.model.RegionContentsUiState
import com.on.turip.feature.popularregion.impl.model.RegionHeatPoint
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * @param baseMonth 방문자 수 기준월(`yyyyMM`). 서버에 수집된 데이터가 없으면 null 이다.
 * @param regions 시도 층과 인기 관광지 층을 합친 목록. 각 원소가 어느 층인지는
 * [PopularRegionModel.isDestination] 로 구분한다.
 */
@Immutable
data class PopularRegionState(
    val baseMonth: String? = null,
    val regions: ImmutableList<PopularRegionModel> = persistentListOf(),
    val selectedRegionCode: String? = null,
    val contentsUiState: RegionContentsUiState = RegionContentsUiState.Unsupported,
    val isLoading: Boolean = true,
    val errorUiState: ErrorUiState = ErrorUiState.None,
) : UiState {
    val selectedRegion: PopularRegionModel? =
        regions.firstOrNull { it.code == selectedRegionCode }

    /**
     * 지도에 칠할 열 목록.
     *
     * 층마다 따로 정규화한다. 시도(수백만)와 관광지(수십만)를 한 자로 재면
     * 작은 시는 늘 가장 옅게 나와 순위를 읽을 수 없다.
     *
     * 인기 관광지를 앞에 둬 라벨 자리를 먼저 잡게 한다. 겹칠 때 살아남는 쪽이
     * 눌러서 콘텐츠를 볼 수 있는 지역이어야 하기 때문이다.
     *
     * 두 층은 도착 시점이 다르다. 상단 칩을 빨리 세우려고 관광지 층만 먼저 반영하는데,
     * 그때 지도를 그리면 시도 배경 없이 관광지 14곳만 뜬 반쪽 지도가 보인다.
     * 두 층이 다 모이는 [isLoading] 종료까지 지도는 비워 둔다.
     */
    val heatPoints: ImmutableList<RegionHeatPoint> =
        if (isLoading) {
            persistentListOf()
        } else {
            buildList {
                val (destinations: List<PopularRegionModel>, sidoRegions: List<PopularRegionModel>) =
                    regions.partition { it.isDestination }
                addAll(destinations.toHeatPoints())
                addAll(sidoRegions.toHeatPoints())
            }.toImmutableList()
        }

    /**
     * 상단 칩 줄에 늘어놓을 인기 관광지. 순위 오름차순이다.
     *
     * 지도에서 속초·수원처럼 작은 지역은 손가락으로 정확히 누르기 어렵다.
     * 칩은 그 지역들로 가는 두 번째 길이고, 고르면 지도를 탭한 것과 같은 선택이 일어난다.
     */
    val destinationChips: ImmutableList<PopularRegionModel> =
        regions
            .filter { it.isDestination }
            .sortedBy { it.rank ?: Int.MAX_VALUE }
            .toImmutableList()

    /** 방문자 수 1위 관광지. 시트에 `인기 지역` 배지를 붙일지 판단한다. */
    val topRegionCode: String? =
        regions.firstOrNull { it.rank == TOP_RANK }?.code

    val shouldShowSheet: Boolean = selectedRegion != null

    val shouldShowMapHint: Boolean = !isLoading && selectedRegion == null

    /** 기준월을 화면에 적기 위해 `202506` 을 연/월로 나눠 둔다. 형식이 다르면 둘 다 null 이다. */
    val baseYear: Int? = baseMonth.toBaseYear()

    val baseMonthOfYear: Int? = baseMonth.toBaseMonthOfYear()

    /** 한 층을 그 층의 최댓값 기준으로 정규화한다. 방문자 수 내림차순이라 첫 원소가 최댓값이다. */
    private fun List<PopularRegionModel>.toHeatPoints(): List<RegionHeatPoint> {
        val maxVisitorCount: Long = firstOrNull()?.visitorCount ?: return emptyList()
        return map { region ->
            RegionHeatPoint(
                shapeKey = region.shapeKey,
                name = region.name,
                location = region.location,
                intensity = (region.visitorCount.toFloat() / maxVisitorCount)
                    .coerceIn(MIN_INTENSITY, 1f),
                isLabeled = true,
            )
        }
    }

    companion object {
        /** 열이 아예 안 보이는 지역이 없도록 하한을 둔다. */
        private const val MIN_INTENSITY: Float = 0.12f
        private const val TOP_RANK: Int = 1
    }
}
