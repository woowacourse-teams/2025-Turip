package com.on.turip.core.ui.error

import androidx.compose.runtime.Immutable
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.cannot_connect_network
import com.on.turip.core.designsystem.generated.resources.check_connection_status
import com.on.turip.core.designsystem.generated.resources.ic_network_error
import com.on.turip.core.designsystem.generated.resources.ic_server_error
import com.on.turip.core.designsystem.generated.resources.mascot
import com.on.turip.core.designsystem.generated.resources.retry
import com.on.turip.core.designsystem.generated.resources.retry_later
import com.on.turip.core.designsystem.generated.resources.server_error
import com.on.turip.core.designsystem.generated.resources.unexpected_error

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
                imageRes = Res.drawable.ic_server_error,
                titleRes = Res.string.server_error,
                descriptionRes = Res.string.retry_later,
                retryTextRes = Res.string.retry,
            )
        }

        ErrorUiState.Network -> {
            ErrorUiModel(
                imageRes = Res.drawable.ic_network_error,
                titleRes = Res.string.cannot_connect_network,
                descriptionRes = Res.string.check_connection_status,
                retryTextRes = Res.string.retry,
            )
        }

        ErrorUiState.Unexpected -> {
            ErrorUiModel(
                imageRes = Res.drawable.mascot,
                titleRes = Res.string.unexpected_error,
                descriptionRes = Res.string.retry_later,
                retryTextRes = Res.string.retry,
            )
        }

        else -> {
            null
        }
    }
