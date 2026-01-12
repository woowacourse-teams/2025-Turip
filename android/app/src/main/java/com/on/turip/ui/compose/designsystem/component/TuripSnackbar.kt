package com.on.turip.ui.compose.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun TuripSnackbar(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { snackbarData: SnackbarData ->
            Snackbar(
                modifier = modifier.padding(TuripTheme.spacing.large),
                action = {
                    snackbarData.visuals.actionLabel?.let { label ->
                        TextButton(onClick = snackbarData::performAction) {
                            Text(
                                text = label,
                                style = TuripTheme.typography.info2,
                                color = TuripTheme.colors.gray02,
                            )
                        }
                    }
                },
            ) {
                Text(
                    text = snackbarData.visuals.message,
                    style = TuripTheme.typography.info1,
                    maxLines = 1,
                )
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun TuripSnackbarPreview() {
    val snackbarHostState = remember { SnackbarHostState() }

    TuripTheme {
        LaunchedEffect(Unit) {
            snackbarHostState.showSnackbar(
                message = "미리보기 스낵바",
            )
        }

        TuripSnackbar(
            snackbarHostState = snackbarHostState,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
