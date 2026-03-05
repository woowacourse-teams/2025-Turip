package com.on.turip.ui.common.error

import androidx.compose.runtime.Immutable
import com.on.turip.R

/**
 * 모든 ViewModel 에서 동시에 발생할 수 없는 에러 상태인 ServerError, NetworkError를 관리하는 UiState
 */
@Immutable
sealed interface ErrorUiState {
    data object None : ErrorUiState

    data object Unexpected : ErrorUiState

    data object Server : ErrorUiState

    data object Network : ErrorUiState
}

fun ErrorUiState.toUiModel(): ErrorUiModel? =
    when (this) {
        ErrorUiState.Server -> {
            ErrorUiModel(
                imageRes = R.drawable.ic_server_error,
                titleRes = R.string.server_error,
                descriptionRes = R.string.retry_later,
                retryTextRes = R.string.retry,
            )
        }

        ErrorUiState.Network -> {
            ErrorUiModel(
                imageRes = R.drawable.ic_network_error,
                titleRes = R.string.cannot_connect_network,
                descriptionRes = R.string.check_connection_status,
                retryTextRes = R.string.retry,
            )
        }

        ErrorUiState.Unexpected -> {
            ErrorUiModel(
                imageRes = R.drawable.mascot,
                titleRes = R.string.unexpected_error,
                descriptionRes = R.string.retry_later,
                retryTextRes = R.string.retry,
            )
        }

        else -> {
            null
        }
    }
