package com.on.turip.ui.compose.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

/**
 *
 * containerColor : 앱 바 색상
 *
 * contentColor : 앱 바 내부의 요소들 색상 (LocalContentColor는 Image에는 적용 안되므로 주의)
 *
 * start : 앱 바 기준 시작점에서부터 Row 정렬하기 위한 컴포넌트들
 *
 * center : 시작, 끝 지점에 아이콘이 있더라도 중앙 정렬하기 위한 컴포넌트
 *
 * end : 앱 바 기준 끝지점에 Row 정렬하기 위한 컴포넌트들
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
                .height(58.dp) // xml과 로고 맞추기 위한 dp값, 나중에 피그마 기준인 62로 수정 필요
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

private sealed class AppBarPreviewCase {
    data object BackOnly : AppBarPreviewCase()

    data object LogoOnly : AppBarPreviewCase()

    data object BackAndTitle : AppBarPreviewCase()

    data object BackAndActions : AppBarPreviewCase()
}

private class AppBarPreviewProvider : PreviewParameterProvider<AppBarPreviewCase> {
    override val values: Sequence<AppBarPreviewCase> =
        sequenceOf(
            AppBarPreviewCase.BackOnly,
            AppBarPreviewCase.LogoOnly,
            AppBarPreviewCase.BackAndTitle,
            AppBarPreviewCase.BackAndActions,
        )
}

@Preview(showBackground = true)
@Composable
private fun TuripAppBarPreview(
    @PreviewParameter(AppBarPreviewProvider::class)
    case: AppBarPreviewCase,
) {
    TuripTheme {
        when (case) {
            AppBarPreviewCase.BackOnly -> {
                TuripAppBar(
                    start = {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    },
                )
            }

            AppBarPreviewCase.LogoOnly -> {
                TuripAppBar(
                    start = {
                        Image(painterResource(R.drawable.ic_logo), null)
                    },
                )
            }

            AppBarPreviewCase.BackAndTitle -> {
                val startAreaSize = 30.dp
                TuripAppBar(
                    start = {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    },
                    center = {
                        Text(
                            text = "text lenght is very Loooooooooooooooooooooooooooong ",
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.padding(start = startAreaSize),
                        )
                    },
                )
            }

            AppBarPreviewCase.BackAndActions -> {
                TuripAppBar(
                    start = {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                    },
                    end = {
                        Icon(Icons.Default.MoreVert, null)
                    },
                )
            }
        }
    }
}
