package com.on.turip.core.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.error.ErrorUiModel
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.error.toUiModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ErrorScreen(
    errorUiState: ErrorUiState,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val errorUiModel: ErrorUiModel = errorUiState.toUiModel() ?: return

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(TuripTheme.spacing.large),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(errorUiModel.imageRes),
            contentDescription = null,
            modifier = Modifier.size(120.dp),
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

        Text(
            text = stringResource(errorUiModel.titleRes),
            style = TuripTheme.typography.title1,
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.small))

        Text(
            text = stringResource(errorUiModel.descriptionRes),
            style = TuripTheme.typography.body1,
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

        Button(onClick = onRetryClick) {
            Text(text = stringResource(errorUiModel.retryTextRes))
        }
    }
}

@Preview(showBackground = true, name = "네트워크 에러")
@Composable
private fun NetworkErrorScreenPreview() {
    TuripTheme {
        ErrorScreen(
            errorUiState = ErrorUiState.Network,
            onRetryClick = {},
        )
    }
}

@Preview(showBackground = true, name = "서버 에러")
@Composable
private fun ServerErrorScreenPreview() {
    TuripTheme {
        ErrorScreen(
            errorUiState = ErrorUiState.Server,
            onRetryClick = {},
        )
    }
}

@Preview(showBackground = true, name = "서버, 네트워크 외 에러")
@Composable
private fun UnknownErrorScreenPreview() {
    TuripTheme {
        ErrorScreen(
            errorUiState = ErrorUiState.Unexpected,
            onRetryClick = {},
        )
    }
}
