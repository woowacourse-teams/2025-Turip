package com.on.turip.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * @param container 영상 상세 장소, 검색 결과 컨텐츠 등 감싸는 영역의 Round (8.dp)
 * @param largeContainer TuripDialog 카드나 검색창 등의 Round (16.dp)
 * @param chip 장소, 날짜 등 Chip의 Round (12.dp)
 * @param bottomSheetRounded bottomSheet의 top Round (topStart = 16.dp, topEnd = 16.dp)
 * @param wideButton 화면 가로 영역을 가득 채우는 버튼의 Round (24.dp)
 */
@Immutable
data class TuripShape(
    val container: Shape,
    val largeContainer: Shape,
    val chip: Shape,
    val bottomSheetRounded: Shape,
    val wideButton: Shape,
)

internal val Shape =
    TuripShape(
        container = RoundedCornerShape(8.dp),
        largeContainer = RoundedCornerShape(16.dp),
        chip = RoundedCornerShape(12.dp),
        bottomSheetRounded = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        wideButton = RoundedCornerShape(24.dp),
    )

val LocalShape =
    staticCompositionLocalOf {
        TuripShape(
            container = RoundedCornerShape(0.dp),
            largeContainer = RoundedCornerShape(0.dp),
            chip = RoundedCornerShape(0.dp),
            bottomSheetRounded = RoundedCornerShape(0.dp),
            wideButton = RoundedCornerShape(0.dp),
        )
    }
