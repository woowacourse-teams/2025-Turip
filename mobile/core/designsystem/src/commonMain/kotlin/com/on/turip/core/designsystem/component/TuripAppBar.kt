package com.on.turip.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.theme.TuripTheme

/**
 * @param containerColor 앱 바 색상
 * @param contentColor 앱 바 내부의 요소들 색상 (LocalContentColor는 Image에는 적용 안되므로 주의)
 * @param start 앱 바 기준 시작점에서부터 Row 정렬하기 위한 컴포넌트들
 * @param center 시작, 끝 지점에 아이콘이 있더라도 중앙 정렬하기 위한 컴포넌트
 * @param end 앱 바 기준 끝지점에 Row 정렬하기 위한 컴포넌트들
 */
@Composable
fun TuripAppBar(
    modifier: Modifier = Modifier,
    containerColor: Color = TuripTheme.colors.white,
    contentColor: Color = TuripTheme.colors.black,
    start: (@Composable RowScope.() -> Unit)? = null,
    center: (@Composable () -> Unit)? = null,
    end: (@Composable RowScope.() -> Unit)? = null,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(58.dp)
                .background(containerColor)
                .padding(horizontal = TuripTheme.spacing.extraLarge)
                .pointerInput(Unit) {}
                .then(modifier),
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
        ) {
            if (start != null) {
                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically,
                    content = start,
                )
            }

            if (center != null) {
                Box(
                    modifier = Modifier.align(Alignment.Center),
                    contentAlignment = Alignment.Center,
                ) {
                    center()
                }
            }

            if (end != null) {
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically,
                    content = end,
                )
            }
        }
    }
}
