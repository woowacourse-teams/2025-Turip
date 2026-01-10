package com.on.turip.ui.compose.trip.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun DiagonalBackground(
    modifier: Modifier = Modifier,
    backgroundColor: Color = TuripTheme.colors.primary,
) {
    Canvas(
        modifier =
            modifier
                .drawWithCache {
                    val path =
                        Path().apply {
                            // 좌상단
                            moveTo(0f, 0f)

                            // 선 시작점
                            lineTo(
                                size.width * 0f,
                                size.height * 0.9f,
                            )

                            // 꺾인 선
                            lineTo(
                                size.width * 0.53f,
                                size.height * 0.5f,
                            )
                            lineTo(
                                size.width * 0.54f,
                                size.height * 1f,
                            )
                            lineTo(
                                size.width * 1f,
                                size.height * 0.7f,
                            )

                            // 우상단
                            lineTo(size.width, 0f)

                            close()
                        }

                    onDrawBehind {
                        drawPath(
                            path = path,
                            color = backgroundColor,
                        )
                    }
                },
    ) {
    }
}

@Preview(showBackground = true)
@Composable
private fun DiagonalBackgroundPreview() {
    TuripTheme {
        DiagonalBackground(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(120.dp),
        )
    }
}
