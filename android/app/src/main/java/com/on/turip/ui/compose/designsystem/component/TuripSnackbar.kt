package com.on.turip.ui.compose.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
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
        snackbar = {
            Snackbar(modifier = modifier.padding(16.dp)) {
                Text(
                    text = it.visuals.message,
                    style = TuripTypography.labelLarge,
                    color = colorResource(R.color.pure_white_ffffff),
                    maxLines = 1,
                )
            }
        },
    )
}
