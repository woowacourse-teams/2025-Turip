package com.on.turip.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_close_description
import com.on.turip.core.designsystem.model.SnackbarIconModel
import com.on.turip.core.designsystem.theme.TuripTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun TuripSnackbar(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
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
                                    text = stringResource(Res.string.all_close_description),
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
                        if (visuals is TuripSnackbarVisuals) {
                            when (val icon = visuals.icon) {
                                is SnackbarIconModel.Vector -> {
                                    Icon(
                                        imageVector = icon.imageVector,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }

                                is SnackbarIconModel.PainterIcon -> {
                                    Image(
                                        painter = icon.painter,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }

                                null -> {}
                            }
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
    val icon: SnackbarIconModel? = null,
) : SnackbarVisuals
