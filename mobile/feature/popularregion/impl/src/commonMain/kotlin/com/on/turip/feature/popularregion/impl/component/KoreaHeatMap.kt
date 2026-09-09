package com.on.turip.feature.popularregion.impl.component

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.popularregion.impl.map.KoreaMapProjection
import com.on.turip.feature.popularregion.impl.map.KoreaOutline
import com.on.turip.feature.popularregion.impl.map.RegionCells
import com.on.turip.feature.popularregion.impl.model.RegionHeatPoint
import kotlinx.collections.immutable.ImmutableList
import kotlin.math.min

/**
 * 대한민국 인기 지역 히트맵.
 *
 * 지도 SDK 없이 Compose Canvas 로만 그린다.
 * - 땅: [KoreaOutline] 의 해안선 폴리곤 하나 (실제 행정경계 데이터는 쓰지 않는다)
 * - 구획: [RegionCells] 가 만든 보로노이 셀. 탭 판정 규칙을 그대로 도형으로 바꾼 것이라,
 *   화면에 보이는 선과 실제로 눌리는 영역이 항상 일치한다.
 * - 열: 지역 중심점 기준 방사형 그라데이션. 바다로 번지지 않도록 땅 Path 로 잘라낸다.
 * - 탭: 육지를 누르면 그 지점이 속한 구획의 지역이, 바다를 누르면 [onEmptyClick] 이 불린다.
 *
 * @param heatPoints 이미 0..1 로 정규화된 열 목록 ([RegionHeatPoint.intensity])
 * @param bottomInset 바텀시트가 아래에서 덮는 높이. 캔버스(바다)는 항상 화면을 꽉 채우고,
 * 지도만 이 높이를 뺀 영역에 맞춰진다. 시트가 없으면 0 이라 지도가 화면 전체를 쓴다.
 */
@Composable
internal fun KoreaHeatMap(
    heatPoints: ImmutableList<RegionHeatPoint>,
    selectedRegionCode: String?,
    onRegionClick: (regionCode: String) -> Unit,
    onEmptyClick: () -> Unit,
    modifier: Modifier = Modifier,
    bottomInset: Dp = 0.dp,
) {
    // 라벨 수만큼 캐시를 잡아야 시트가 열리고 닫히는 동안 매 프레임 재측정하지 않는다.
    val textMeasurer: TextMeasurer = rememberTextMeasurer(cacheSize = LABEL_CACHE_SIZE)

    // 시트 높이는 애니메이션으로 매 프레임 바뀐다. 제스처 감지기를 다시 만들지 않도록
    // pointerInput 의 key 로 쓰지 않고 최신 값만 읽는다.
    val currentBottomInset: Dp by rememberUpdatedState(bottomInset)
    val labelStyle: TextStyle =
        TuripTheme.typography.info1.copy(
            color = PopularRegionMapPalette.Label,
            fontSize = LABEL_FONT_SIZE,
        )
    val selectedLabelStyle: TextStyle =
        labelStyle.copy(color = PopularRegionMapPalette.SelectedLabel)

    Box(
        modifier =
            modifier
                .pointerInput(heatPoints) {
                    detectTapGestures { tap: Offset ->
                        val projection: KoreaMapProjection =
                            KoreaMapProjection.fit(
                                size = Size(size.width.toFloat(), size.height.toFloat()),
                                bottomInset = currentBottomInset.toPx(),
                            )
                        // 육지 안이면 반드시 어느 구획엔가 속한다. 구획 = 가장 가까운 중심점.
                        if (!KoreaOutline.contains(projection, tap)) {
                            onEmptyClick()
                            return@detectTapGestures
                        }
                        val nearest: RegionHeatPoint? =
                            heatPoints.minByOrNull { point ->
                                (projection.project(point.location) - tap).getDistanceSquared()
                            }
                        if (nearest == null) onEmptyClick() else onRegionClick(nearest.code)
                    }
                }.drawWithCache {
                    // 크기나 시트 높이가 바뀔 때만 Path·구획·텍스트 레이아웃을 다시 만든다.
                    val projection: KoreaMapProjection =
                        KoreaMapProjection.fit(size = size, bottomInset = bottomInset.toPx())
                    val landPath: Path = KoreaOutline.buildPath(projection)
                    val minDimension: Float = min(size.width, size.height)
                    val centers: List<Offset> =
                        heatPoints.map { projection.project(it.location) }
                    val blobs: List<Pair<Offset, Float>> =
                        centers.mapIndexed { index, center -> center to heatPoints[index].intensity }

                    val cellPaths: List<Path> =
                        RegionCells.build(sites = centers, size = size).map { it.toPath() }
                    val selectedIndex: Int =
                        heatPoints.indexOfFirst { it.code == selectedRegionCode }
                    val selectedCellPath: Path? = cellPaths.getOrNull(selectedIndex)
                    val selectedCenter: Offset? = centers.getOrNull(selectedIndex)

                    val labelGap: Float = LABEL_GAP.toPx()

                    // 지역 중심이 서로 가까우면 이름이 겹쳐 둘 다 못 읽게 된다.
                    // [heatPoints] 는 방문자 수 내림차순이라, 먼저 자리를 잡은 쪽이 이긴다.
                    // 라벨이 밀린 지역도 구획선은 그대로 보이고 눌러서 이름을 확인할 수 있다.
                    val labels: List<MapLabel> =
                        buildList {
                            val placedBounds = ArrayList<Rect>(heatPoints.size)
                            val labelPadding: Float = LABEL_COLLISION_PADDING.toPx()

                            heatPoints.forEachIndexed { index, point ->
                                if (!point.isLabeled) return@forEachIndexed

                                val isSelected: Boolean = index == selectedIndex
                                val layout: TextLayoutResult =
                                    textMeasurer.measure(
                                        text = point.name,
                                        style = if (isSelected) selectedLabelStyle else labelStyle,
                                    )
                                val anchor: Offset = centers[index]
                                val bounds: Rect =
                                    Rect(
                                        left = anchor.x - (layout.size.width / 2f),
                                        top = anchor.y + labelGap,
                                        right = anchor.x + (layout.size.width / 2f),
                                        bottom = anchor.y + labelGap + layout.size.height,
                                    ).inflate(labelPadding)

                                // 지금 보고 있는 지역의 이름은 겹치더라도 반드시 남긴다.
                                if (!isSelected && placedBounds.any { it.overlaps(bounds) }) {
                                    return@forEachIndexed
                                }

                                placedBounds.add(bounds)
                                add(MapLabel(layout = layout, anchor = anchor))
                            }
                        }
                    val landStroke: Float = LAND_STROKE_WIDTH.toPx()
                    val cellStroke: Float = CELL_STROKE_WIDTH.toPx()
                    val selectedCellStroke: Float = SELECTED_CELL_STROKE_WIDTH.toPx()
                    val pinMetrics = PinMetrics(PIN_RADIUS.toPx(), PIN_TAIL_HEIGHT.toPx())

                    onDrawBehind {
                        drawRect(color = PopularRegionMapPalette.Sea)
                        clipPath(landPath) {
                            drawRect(color = PopularRegionMapPalette.Land)
                            blobs.forEach { (center, intensity) ->
                                drawHeatBlob(
                                    center = center,
                                    intensity = intensity,
                                    minDimension = minDimension,
                                )
                            }
                            // 구획선은 땅 안쪽에서만 보여야 바다 위에 선이 떠다니지 않는다.
                            cellPaths.forEach { cell ->
                                drawPath(
                                    path = cell,
                                    color = PopularRegionMapPalette.CellLine,
                                    style = Stroke(width = cellStroke),
                                )
                            }
                            selectedCellPath?.let { cell ->
                                drawPath(
                                    path = cell,
                                    color = PopularRegionMapPalette.SelectedCellLine,
                                    style = Stroke(width = selectedCellStroke),
                                )
                            }
                        }
                        drawPath(
                            path = landPath,
                            color = PopularRegionMapPalette.LandLine,
                            style = Stroke(width = landStroke),
                        )
                        labels.forEach { label ->
                            drawText(
                                textLayoutResult = label.layout,
                                topLeft =
                                    Offset(
                                        x = label.anchor.x - (label.layout.size.width / 2f),
                                        y = label.anchor.y + labelGap,
                                    ),
                            )
                        }
                        selectedCenter?.let { drawPin(center = it, metrics = pinMetrics) }
                    }
                },
    )
}

private fun List<Offset>.toPath(): Path =
    Path().apply {
        if (this@toPath.isEmpty()) return@apply
        moveTo(this@toPath[0].x, this@toPath[0].y)
        for (index in 1..this@toPath.lastIndex) {
            lineTo(this@toPath[index].x, this@toPath[index].y)
        }
        close()
    }

private data class MapLabel(
    val layout: TextLayoutResult,
    val anchor: Offset,
)

private data class PinMetrics(
    val radius: Float,
    val tailHeight: Float,
)

private fun DrawScope.drawHeatBlob(
    center: Offset,
    intensity: Float,
    minDimension: Float,
) {
    val radius: Float =
        minDimension * (BLOB_MIN_RADIUS_RATIO + (BLOB_RADIUS_SPAN_RATIO * intensity))
    val coreColor: Color =
        PopularRegionMapPalette.HeatHigh.copy(
            alpha = BLOB_MIN_ALPHA + ((BLOB_MAX_ALPHA - BLOB_MIN_ALPHA) * intensity),
        )
    drawCircle(
        brush =
            Brush.radialGradient(
                colors = listOf(coreColor, coreColor.copy(alpha = 0f)),
                center = center,
                radius = radius,
            ),
        radius = radius,
        center = center,
    )
}

/** 선택 지역에 꽂히는 물방울 핀. 꼬리 끝이 지역 중심에 닿도록 위로 띄워 그린다. */
private fun DrawScope.drawPin(
    center: Offset,
    metrics: PinMetrics,
) {
    val headCenter = Offset(center.x, center.y - metrics.tailHeight - metrics.radius)
    val tail =
        Path().apply {
            moveTo(center.x, center.y)
            lineTo(center.x - (metrics.radius * PIN_TAIL_WIDTH_RATIO), headCenter.y)
            lineTo(center.x + (metrics.radius * PIN_TAIL_WIDTH_RATIO), headCenter.y)
            close()
        }
    drawPath(path = tail, color = PopularRegionMapPalette.HeatHigh)
    drawCircle(
        color = PopularRegionMapPalette.HeatHigh,
        radius = metrics.radius,
        center = headCenter,
    )
    drawCircle(
        color = Color.White,
        radius = metrics.radius * PIN_HOLE_RATIO,
        center = headCenter,
    )
}

private const val LABEL_CACHE_SIZE: Int = 32
private val LABEL_FONT_SIZE = 11.sp
private val LABEL_GAP = 6.dp

/** 라벨끼리 이만큼 떨어져 있지 않으면 겹친 것으로 본다. */
private val LABEL_COLLISION_PADDING = 2.dp
private val LAND_STROKE_WIDTH = 1.dp
private val CELL_STROKE_WIDTH = 1.dp
private val SELECTED_CELL_STROKE_WIDTH = 2.dp
private val PIN_RADIUS = 9.dp
private val PIN_TAIL_HEIGHT = 8.dp

private const val PIN_TAIL_WIDTH_RATIO: Float = 0.62f
private const val PIN_HOLE_RATIO: Float = 0.38f
private const val BLOB_MIN_RADIUS_RATIO: Float = 0.055f
private const val BLOB_RADIUS_SPAN_RATIO: Float = 0.10f
private const val BLOB_MIN_ALPHA: Float = 0.18f
private const val BLOB_MAX_ALPHA: Float = 0.92f
