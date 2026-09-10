package com.on.turip.feature.popularregion.impl.component

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
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
import com.on.turip.feature.popularregion.impl.map.GeoPoint
import com.on.turip.feature.popularregion.impl.map.KoreaMapProjection
import com.on.turip.feature.popularregion.impl.map.KoreaRegionShapes
import com.on.turip.feature.popularregion.impl.map.RegionShapeKey
import com.on.turip.feature.popularregion.impl.map.containsProjected
import com.on.turip.feature.popularregion.impl.map.geoArea
import com.on.turip.feature.popularregion.impl.map.shapes
import com.on.turip.feature.popularregion.impl.map.toUnionPath
import com.on.turip.feature.popularregion.impl.model.RegionHeatPoint
import kotlinx.collections.immutable.ImmutableList

/**
 * 대한민국 인기 지역 히트맵.
 *
 * 지도 SDK 없이 Compose Canvas 로만 그린다.
 * - 경계: 실제 행정경계 폴리곤. 시도는 [KoreaRegionShapes], 관광지는
 *   [com.on.turip.feature.popularregion.impl.map.PopularDestinationShapes] 가 갖고 있다.
 * - 색: 지역을 방문자 수에 따라 통째로 칠하는 단계구분도(choropleth)
 * - 탭: 누른 점을 품는 폴리곤을 찾는다. 넓이가 작은 지역이 우선이라
 *   전남 안의 광주처럼 감싸인 지역도 정확히 잡힌다.
 *   **다만 인기 관광지 층만 눌린다.** 아래층 시도(경기·전남광주 등)는 튜립이 다루는 지역이 아니라
 *   열어 보여 줄 내용이 없다. 눌리면 빈 시트가 열리므로 빈 곳을 누른 것과 같이 다룬다.
 *
 * 좁은 지역이 넓은 지역에 덮이지 않도록, 칠하기는 넓은 쪽부터 하고 탭 판정은 그 반대로 본다.
 * 관광지(강릉)는 언제나 자기가 속한 시도(강원)보다 좁으므로 이 한 가지 규칙으로 두 층이 함께 정렬된다.
 * 두 층은 같은 붉은 띠를 쓰되 **층마다 따로 정규화**한다. 시도 값으로 관광지를 칠하면
 * 작은 시(속초)가 늘 가장 옅게 나와 순위를 읽을 수 없다.
 *
 * @param heatPoints 방문자 수 내림차순. [RegionHeatPoint.intensity] 는 0..1 로 정규화돼 있다.
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

    // 경계 폴리곤은 지역 목록이 바뀔 때만 다시 찾으면 된다.
    // 넓은 지역이 앞에 오도록 정렬해 두고, 탭은 뒤에서부터 본다.
    //
    // 방문자 수가 오기 전에는 지도가 통째로 비어 버리지 않도록 아는 시도를 전부 그린다.
    // 경계는 앱이 갖고 있으니 응답을 기다릴 이유가 없다. 이때는 값을 모르는 상태라
    // 칠하지 않고([RegionShape.heatPointIndex] 가 null), 이름도 붙이지 않고, 눌러도 반응하지 않는다.
    val shapes: List<RegionShape> =
        remember(heatPoints) {
            val sources: List<Pair<Int?, RegionShapeKey>> =
                if (heatPoints.isEmpty()) {
                    KoreaRegionShapes.areaCodes.map { areaCode -> null to RegionShapeKey.Sido(areaCode) }
                } else {
                    heatPoints.mapIndexed { index, point -> index to point.shapeKey }
                }

            sources
                .mapNotNull { (heatPointIndex, shapeKey) ->
                    val rings: List<List<GeoPoint>> = shapeKey.shapes()
                    if (rings.isEmpty()) {
                        null
                    } else {
                        RegionShape(
                            heatPointIndex = heatPointIndex,
                            rings = rings,
                            area = rings.geoArea(),
                            isSelectable =
                                heatPointIndex != null && heatPoints[heatPointIndex].isDestination,
                        )
                    }
                }.sortedByDescending { it.area }
        }

    // 시트가 열리고 닫히는 동안 bottomInset 이 매 프레임 바뀐다. 그때마다 3천 점짜리
    // 폴리곤을 다시 합집합 내면 애니메이션이 끊기므로, 캔버스 크기가 그대로면 만들어 둔 Path 를 쓴다.
    val pathCache: RegionPathCache = remember { RegionPathCache() }

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
                .pointerInput(shapes) {
                    detectTapGestures { tap: Offset ->
                        val projection: KoreaMapProjection =
                            KoreaMapProjection.fit(
                                size = Size(size.width.toFloat(), size.height.toFloat()),
                                bottomInset = currentBottomInset.toPx(),
                            )
                        // 좁은 지역부터 본다. 관광지는 자기가 속한 시도 링 안에도 들어 있어서
                        // 넓은 쪽부터 보면 수원을 영영 고를 수 없다.
                        //
                        // 시도는 아예 후보에서 뺀다. 관광지 밖을 누르면 그 아래 시도가 잡히는데,
                        // 그걸 선택으로 치면 보여 줄 내용이 없는 시트가 열린다.
                        val hit: RegionShape? =
                            shapes.lastOrNull { shape ->
                                shape.isSelectable && shape.rings.containsProjected(projection, tap)
                            }
                        val hitIndex: Int? = hit?.heatPointIndex
                        if (hitIndex == null) {
                            onEmptyClick()
                        } else {
                            onRegionClick(heatPoints[hitIndex].code)
                        }
                    }
                }.drawWithCache {
                    // 폴리곤은 시트를 아직 열지 않은 상태(inset 0)를 기준으로 한 번만 만든다.
                    // 시트가 덮는 만큼의 차이는 그릴 때 배율·이동으로 맞춘다.
                    val baseProjection: KoreaMapProjection =
                        KoreaMapProjection.fit(size = size)
                    val projection: KoreaMapProjection =
                        KoreaMapProjection.fit(size = size, bottomInset = bottomInset.toPx())
                    val transform: KoreaMapProjection.Transform =
                        baseProjection.transformTo(projection)

                    val selectedIndex: Int =
                        heatPoints.indexOfFirst { it.code == selectedRegionCode }

                    val paths: List<Path> = pathCache.pathsOf(shapes, size, baseProjection)
                    val regions: List<RegionDrawing> =
                        shapes.mapIndexed { index, shape ->
                            val point: RegionHeatPoint? =
                                shape.heatPointIndex?.let { heatPoints[it] }
                            RegionDrawing(
                                path = paths[index],
                                color =
                                    if (point == null) {
                                        PopularRegionMapPalette.UnknownLand
                                    } else {
                                        lerp(
                                            PopularRegionMapPalette.HeatLow,
                                            PopularRegionMapPalette.HeatHigh,
                                            point.intensity,
                                        )
                                    },
                                isSelected = shape.heatPointIndex == selectedIndex,
                            )
                        }

                    val labelGap: Float = LABEL_GAP.toPx()

                    // 지역 중심이 서로 가까우면 이름이 겹쳐 둘 다 못 읽게 된다.
                    // [heatPoints] 는 방문자 수 내림차순이라, 먼저 자리를 잡은 쪽이 이긴다.
                    // 라벨이 밀린 지역도 경계선은 그대로 보이고 눌러서 이름을 확인할 수 있다.
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
                                val anchor: Offset = projection.project(point.location)
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

                    val selectedCenter: Offset? =
                        heatPoints.getOrNull(selectedIndex)?.let { projection.project(it.location) }

                    // 지도를 줄여 그리면 선도 같이 얇아진다. 배율로 나눠 두께를 원래대로 되돌린다.
                    val boundaryStroke: Float = BOUNDARY_STROKE_WIDTH.toPx() / transform.scale
                    val selectedStroke: Float = SELECTED_BOUNDARY_STROKE_WIDTH.toPx() / transform.scale
                    val seamStroke: Float = SEAM_STROKE_WIDTH.toPx() / transform.scale
                    val pinMetrics = PinMetrics(PIN_RADIUS.toPx(), PIN_TAIL_HEIGHT.toPx())

                    onDrawBehind {
                        drawRect(color = PopularRegionMapPalette.Sea)

                        withTransform({
                            translate(left = transform.translateX, top = transform.translateY)
                            scale(
                                scaleX = transform.scale,
                                scaleY = transform.scale,
                                pivot = Offset.Zero,
                            )
                        }) {
                            regions.forEach { region ->
                                drawPath(path = region.path, color = region.color)
                                // 경계를 각자 단순화한 탓에 맞닿은 선이 1px 어긋날 수 있다.
                                // 자기 색으로 얇게 덧그어 그 실금 사이로 바다가 비치지 않게 한다.
                                drawPath(
                                    path = region.path,
                                    color = region.color,
                                    style = Stroke(width = seamStroke),
                                )
                            }

                            regions.forEach { region ->
                                drawPath(
                                    path = region.path,
                                    color = PopularRegionMapPalette.BoundaryLine,
                                    style = Stroke(width = boundaryStroke),
                                )
                            }

                            // 선택한 지역의 테두리는 다른 지역에 덮이지 않도록 맨 위에 다시 그린다.
                            regions.firstOrNull { it.isSelected }?.let { region ->
                                drawPath(
                                    path = region.path,
                                    color = PopularRegionMapPalette.SelectedBoundaryLine,
                                    style = Stroke(width = selectedStroke),
                                )
                            }
                        }

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

/**
 * 지역 하나의 경계와 순서.
 *
 * @param heatPointIndex 이 지역이 [KoreaHeatMap] 의 `heatPoints` 에서 몇 번째인지.
 * 방문자 수가 아직 오지 않아 모양만 그리는 지역은 null 이다.
 * @param area 위경도 기준 넓이. 그리는 순서와 탭 우선순위를 정한다.
 * @param isSelectable 눌러서 고를 수 있는지. 인기 관광지 층만 true 다.
 * 시도는 칠하기만 하고 누르면 반응하지 않는다.
 */
private data class RegionShape(
    val heatPointIndex: Int?,
    val rings: List<List<GeoPoint>>,
    val area: Double,
    val isSelectable: Boolean,
)

private data class RegionDrawing(
    val path: Path,
    val color: Color,
    val isSelected: Boolean,
)

private data class MapLabel(
    val layout: TextLayoutResult,
    val anchor: Offset,
)

private data class PinMetrics(
    val radius: Float,
    val tailHeight: Float,
)

/** 선택 지역에 꽂히는 물방울 핀. 꼬리 끝이 지역 중심에 닿도록 위로 띄워 그린다. */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPin(
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

private val BOUNDARY_STROKE_WIDTH = 1.dp
private val SELECTED_BOUNDARY_STROKE_WIDTH = 2.dp

/** 단순화로 생긴 1px 어긋남을 메우는 덧칠 두께 */
private val SEAM_STROKE_WIDTH = 1.dp

private val PIN_RADIUS = 9.dp
private val PIN_TAIL_HEIGHT = 8.dp

private const val PIN_TAIL_WIDTH_RATIO: Float = 0.62f
private const val PIN_HOLE_RATIO: Float = 0.38f

/**
 * 만들어 둔 지역 [Path] 를 캔버스 크기가 그대로인 동안 다시 쓰는 캐시.
 *
 * 폴리곤 합집합은 3천 점을 다루는 일이라 매 프레임 하기엔 무겁다.
 * 시트가 열고 닫히며 바뀌는 것은 배율과 위치뿐이라, 그 차이는 그릴 때 변환으로 맞춘다.
 */
private class RegionPathCache {
    private var cachedShapes: List<RegionShape>? = null
    private var cachedSize: Size = Size.Unspecified
    private var cachedPaths: List<Path> = emptyList()

    fun pathsOf(
        shapes: List<RegionShape>,
        size: Size,
        projection: KoreaMapProjection,
    ): List<Path> {
        if (cachedShapes === shapes && cachedSize == size) return cachedPaths

        cachedPaths = shapes.map { it.rings.toUnionPath(projection) }
        cachedShapes = shapes
        cachedSize = size
        return cachedPaths
    }
}
