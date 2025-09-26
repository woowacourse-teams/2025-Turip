package com.on.turip.ui.compose.common.component

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.domain.ErrorEvent
import com.on.turip.ui.compose.theme.TuripTypography

@Composable
fun ErrorScreen(
    errorEvent: ErrorEvent,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (imageRes, titleRes, descriptionRes, retryTextRes) =
        when (errorEvent) {
            ErrorEvent.NETWORK_ERROR ->
                listOf(
                    R.drawable.ic_network_error,
                    R.string.cannot_connect_network,
                    R.string.check_connection_status,
                    R.string.retry,
                )

            ErrorEvent.USER_NOT_HAVE_PERMISSION,
            ErrorEvent.DUPLICATION_FOLDER,
            ErrorEvent.UNEXPECTED_PROBLEM,
            ErrorEvent.PARSER_ERROR,
            ->
                listOf(
                    R.drawable.ic_server_error,
                    R.string.server_error,
                    R.string.retry_later,
                    R.string.retry,
                )
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.size(120.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = titleRes),
            style = TuripTypography.titleLarge,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = descriptionRes),
            style = TuripTypography.bodyLarge,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRetryClick) {
            Text(text = stringResource(id = retryTextRes))
        }
    }
}

@Preview(showBackground = true, name = "네트워크 에러 시")
@Composable
private fun NetworkErrorScreenPreview() {
    ErrorScreen(
        errorEvent = ErrorEvent.NETWORK_ERROR,
        onRetryClick = {},
    )
}

@Preview(showBackground = true, name = "그 외 에러 발생")
@Composable
private fun OtherErrorScreenPreview() {
    ErrorScreen(
        errorEvent = ErrorEvent.UNEXPECTED_PROBLEM,
        onRetryClick = {},
    )
}
