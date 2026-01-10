package com.on.turip.ui.compose.designsystem.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * @param container : 영상 상세 장소, 검색 결과 컨텐츠 등 감싸는 영역의 Round RoundedCornerShape(8.dp)
 *
 * @param largeContainer : TuripDialog 카드나 검색창 등의 Round RoundedCornerShape(16.dp)
 *
 * @param chip : 장소, 날짜 등 Chip의 Round RoundedCornerShape(12.dp)
 *
 * @param bottomSheetRounded : bottomSheet의 top Round RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
 *
 * @param wideButton : 화면 가로 영역을 가득 채우는 버튼의 Round RoundedCornerShape(24.dp)
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

@Preview(name = "Turip Shape", heightDp = 800)
@Composable
private fun ShapePreview() {
    TuripTheme {
        val shapes = LocalShape.current

        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier =
                    Modifier
                        .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = "Turip Shape Tokens",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                ShapeItem("container (8.dp)", shapes.container)
                ShapeItem("largeContainer (16.dp)", shapes.largeContainer)
                ShapeItem("chip (12.dp)", shapes.chip)
                ShapeItem("bottomSheetRounded (top 16.dp)", shapes.bottomSheetRounded)
                ShapeItem("wideButton (24.dp)", shapes.wideButton)
            }
        }
    }
}

@Composable
private fun ShapeItem(
    name: String,
    shape: Shape,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
        )
        ShapePreviewBox(
            shape = shape,
            modifier = Modifier.size(100.dp, 60.dp),
        )
    }
}

@Composable
fun ShapePreviewBox(
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier.background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = shape,
            ),
    )
}
