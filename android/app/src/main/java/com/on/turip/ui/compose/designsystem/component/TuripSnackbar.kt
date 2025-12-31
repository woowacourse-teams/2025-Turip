package com.on.turip.ui.compose.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.theme.TuripTypography

@Composable
fun TuripSnackbar(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { snackbarData: SnackbarData ->
            Snackbar(
                modifier = modifier.padding(16.dp),
                action = {
                    snackbarData.visuals.actionLabel?.let { label ->
                        Text(
                            text = label,
                            style = TuripTypography.labelMedium,
                            color = colorResource(R.color.turip_gray_b4b4b4),
                            modifier = Modifier
                                .clickable(onClick = snackbarData::performAction)
                                .padding(end = 10.dp)
                        )
                    }
                }
            ) {
                Text(
                    text = snackbarData.visuals.message,
                    style = TuripTypography.labelLarge,
                    color = colorResource(R.color.pure_white_ffffff),
                    maxLines = 1,
                )
            }
        },
    )
}
