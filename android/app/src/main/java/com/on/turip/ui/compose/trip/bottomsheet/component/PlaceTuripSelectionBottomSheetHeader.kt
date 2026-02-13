package com.on.turip.ui.compose.trip.bottomsheet.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun PlaceTuripSelectionBottomSheetHeader(
    title: String,
    modifier: Modifier = Modifier,
    navigation: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = TuripTheme.spacing.small),
        contentAlignment = Alignment.Center,
    ) {
        Box(modifier = Modifier.align(Alignment.CenterStart)) {
            navigation?.invoke()
        }

        Text(
            text = title,
            style = TuripTheme.typography.title2,
            color = TuripTheme.colors.gray04,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 60.dp),
        )

        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            actions?.invoke(this)
        }
    }
}

private data class PlaceTuripSelectionHeaderState(
    val title: String,
    val hasNavigation: Boolean = false,
    val hasActions: Boolean = false,
)

private class PlaceTuripSelectionHeaderPreviewParameterProvider : PreviewParameterProvider<PlaceTuripSelectionHeaderState> {
    override val values =
        sequenceOf(
            PlaceTuripSelectionHeaderState(title = "기본 튜립"),
            PlaceTuripSelectionHeaderState(title = "아주 아주 아주 아주 아주 아주 길어서 끝이 보이지 않는 튜립 이름입니다"),
            PlaceTuripSelectionHeaderState(title = "네비게이션 포함", hasNavigation = true),
            PlaceTuripSelectionHeaderState(
                title = "편집 모드",
                hasNavigation = true,
                hasActions = true,
            ),
        )
}

@Preview(showBackground = true)
@Composable
private fun PlaceTuripSelectionBottomSheetHeaderPreview(
    @PreviewParameter(PlaceTuripSelectionHeaderPreviewParameterProvider::class) state: PlaceTuripSelectionHeaderState,
) {
    TuripTheme {
        PlaceTuripSelectionBottomSheetHeader(
            title = state.title,
            navigation = {
                if (state.hasNavigation) {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Close, null)
                    }
                }
            },
            actions = {
                if (state.hasActions) {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Check, null)
                    }
                }
            },
        )
    }
}
