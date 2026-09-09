package com.on.turip.feature.popularregion.impl

import androidx.compose.runtime.Immutable
import com.on.turip.core.ui.UiState
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.feature.popularregion.impl.model.PopularRegionModel
import com.on.turip.feature.popularregion.impl.model.RegionContentsUiState
import com.on.turip.feature.popularregion.impl.model.RegionHeatPoint
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * @param baseMonth 방문자 수 기준월(`yyyyMM`). 서버에 수집된 데이터가 없으면 null 이다.
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
     * [regions] 는 방문자 수 내림차순이므로 첫 원소가 곧 최댓값이다.
     * 시도는 17개뿐이라 라벨은 전부 켠다.
     */
    val heatPoints: ImmutableList<RegionHeatPoint> =
        regions
            .let { sorted ->
                val maxVisitorCount: Long = sorted.firstOrNull()?.visitorCount ?: 1L
                sorted.map { region ->
                    RegionHeatPoint(
                        code = region.code,
                        name = region.name,
                        location = region.location,
                        intensity = (region.visitorCount.toFloat() / maxVisitorCount)
                            .coerceIn(MIN_INTENSITY, 1f),
                        isLabeled = true,
                    )
                }
            }.toImmutableList()

    /** 방문자 수 1위 지역. 시트에 `인기 지역` 배지를 붙일지 판단한다. */
    val topRegionCode: String? = regions.firstOrNull()?.code

    val shouldShowSheet: Boolean = selectedRegion != null

    val shouldShowMapHint: Boolean = !isLoading && selectedRegion == null

    /** 기준월을 화면에 적기 위해 `202506` 을 연/월로 나눠 둔다. 형식이 다르면 둘 다 null 이다. */
    val baseYear: Int? = baseMonth?.takeIf { it.length == BASE_MONTH_LENGTH }?.take(YEAR_LENGTH)?.toIntOrNull()

    val baseMonthOfYear: Int? =
        baseMonth
            ?.takeIf { it.length == BASE_MONTH_LENGTH }
            ?.drop(YEAR_LENGTH)
            ?.toIntOrNull()
            ?.takeIf { it in MONTH_RANGE }

    companion object {
        /** 열이 아예 안 보이는 지역이 없도록 하한을 둔다. */
        private const val MIN_INTENSITY: Float = 0.12f
        private const val BASE_MONTH_LENGTH: Int = 6
        private const val YEAR_LENGTH: Int = 4
        private val MONTH_RANGE: IntRange = 1..12
    }
}
