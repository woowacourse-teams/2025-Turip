package com.on.turip.ui.compose.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.common.error.ErrorUiModel
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.toUiModel
import com.on.turip.ui.compose.designsystem.theme.TuripTypography

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
                .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = errorUiModel.imageRes),
            contentDescription = null,
            modifier = Modifier.size(120.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = errorUiModel.titleRes),
            style = TuripTypography.titleLarge,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = errorUiModel.descriptionRes),
            style = TuripTypography.bodyLarge,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            colors =
                ButtonColors(
                    contentColor = colorResource(R.color.pure_white_ffffff),
                    containerColor = colorResource(R.color.turip_blue_11aebf_70),
                    disabledContentColor = colorResource(R.color.pure_white_ffffff),
                    disabledContainerColor = colorResource(R.color.gray_300_5b5b5b),
                ),
            onClick = onRetryClick,
        ) {
            Text(text = stringResource(id = errorUiModel.retryTextRes))
        }
    }
}

@Preview(showBackground = true, name = "네트워크 에러 시")
@Composable
private fun NetworkErrorScreenPreview() {
    ErrorScreen(
        errorUiState = ErrorUiState.Network,
        onRetryClick = {},
    )
}

@Preview(showBackground = true, name = "커스텀 에러 외 에러 발생")
@Composable
private fun AppErrorScreenPreview() {
    ErrorScreen(
        errorUiState = ErrorUiState.Server,
        onRetryClick = {},
    )
}
