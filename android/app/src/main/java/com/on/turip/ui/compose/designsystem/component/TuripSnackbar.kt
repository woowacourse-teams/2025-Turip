package com.on.turip.ui.compose.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun TuripSnackbar(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.imePadding()) {
        SnackbarHost(
            hostState = snackbarHostState,
            snackbar = { snackbarData: SnackbarData ->
                val visuals =
                    snackbarData.visuals as? TuripSnackbarVisuals ?: snackbarData.visuals

                Snackbar(
                    modifier = Modifier.padding(TuripTheme.spacing.large),
                    action = {
                        visuals.actionLabel?.let { label ->
                            TextButton(onClick = snackbarData::performAction) {
                                Text(
                                    text = label,
                                    style = TuripTheme.typography.info2,
                                    color = TuripTheme.colors.gray02,
                                )
                            }
                        }
                    },
                    dismissAction = {
                        if (visuals.withDismissAction) {
                            TextButton(onClick = snackbarData::dismiss) {
                                Text(
                                    text = stringResource(R.string.all_close_description),
                                    style = TuripTheme.typography.info2,
                                    color = TuripTheme.colors.gray02,
                                )
                            }
                        }
                    },
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
                    ) {
                        if (visuals is TuripSnackbarVisuals && visuals.iconRes != null) {
                            Image(
                                painter = painterResource(visuals.iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                        }

                        Text(
                            text = visuals.message,
                            style = TuripTheme.typography.info1,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            },
        )
    }
}

class TuripSnackbarVisuals(
    override val message: String,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    @DrawableRes val iconRes: Int? = null,
) : SnackbarVisuals

private class DefaultSnackbarPreviewModel(
    val message: String,
    val actionLabel: String? = null,
)

private class TuripVisualsSnackbarPreviewModel(
    val message: String,
    val actionLabel: String? = null,
    val withDismissAction: Boolean = false,
    @DrawableRes val iconRes: Int? = null,
)

private class DefaultSnackbarPreviewProvider : PreviewParameterProvider<DefaultSnackbarPreviewModel> {
    override val values: Sequence<DefaultSnackbarPreviewModel> =
        sequenceOf(
            DefaultSnackbarPreviewModel(message = "저장되었습니다"),
            DefaultSnackbarPreviewModel(message = "북마크에 추가되었습니다", actionLabel = "닫기"),
            DefaultSnackbarPreviewModel(message = "네트워크 연결 실패", actionLabel = "재시도"),
        )
}

private class TuripVisualsSnackbarPreviewProvider : PreviewParameterProvider<TuripVisualsSnackbarPreviewModel> {
    override val values: Sequence<TuripVisualsSnackbarPreviewModel> =
        sequenceOf(
            TuripVisualsSnackbarPreviewModel(
                message = "저장되었습니다",
                iconRes = null,
            ),
            TuripVisualsSnackbarPreviewModel(
                message = "북마크 목록에 추가되었습니다",
                iconRes = R.drawable.btn_bookmark_selected,
                actionLabel = "닫기",
            ),
            TuripVisualsSnackbarPreviewModel(
                message = "북마크 목록에서 해제되었습니다",
                iconRes = R.drawable.btn_bookmark_normal,
                actionLabel = "재시도",
            ),
            TuripVisualsSnackbarPreviewModel(
                message = "네트워크 연결 실패",
                iconRes = null,
                actionLabel = "닫기",
            ),
            TuripVisualsSnackbarPreviewModel(
                message = "연결 실패, withDismissAction = true",
                iconRes = null,
                withDismissAction = true,
            ),
        )
}

@Preview(showBackground = true, name = "Snackbar 구성할 때 TuripSnackbarVisuals 사용 X")
@Composable
private fun DefaultSnackbarPreview(
    @PreviewParameter(DefaultSnackbarPreviewProvider::class)
    preview: DefaultSnackbarPreviewModel,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    TuripTheme {
        LaunchedEffect(preview) {
            snackbarHostState.showSnackbar(
                message = preview.message,
                actionLabel = preview.actionLabel,
            )
        }

        TuripSnackbar(
            snackbarHostState = snackbarHostState,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true, name = "Snackbar 구성할 때 TuripSnackbarVisuals 사용 O")
@Composable
private fun TuripVisualsSnackbarPreview(
    @PreviewParameter(TuripVisualsSnackbarPreviewProvider::class)
    preview: TuripVisualsSnackbarPreviewModel,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    TuripTheme {
        LaunchedEffect(preview) {
            snackbarHostState.showSnackbar(
                visuals =
                    TuripSnackbarVisuals(
                        message = preview.message,
                        actionLabel = preview.actionLabel,
                        duration = SnackbarDuration.Short,
                        withDismissAction = preview.withDismissAction,
                        iconRes = preview.iconRes,
                    ),
            )
        }

        TuripSnackbar(
            snackbarHostState = snackbarHostState,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
