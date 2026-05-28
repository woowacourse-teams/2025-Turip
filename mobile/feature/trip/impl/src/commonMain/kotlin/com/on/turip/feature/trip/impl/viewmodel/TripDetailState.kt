package com.on.turip.feature.trip.impl.viewmodel

import androidx.compose.runtime.Immutable
import com.on.turip.core.model.Content
import com.on.turip.core.ui.UiState

@Immutable
data class TripDetailState(
    val content: Content? = null,
    val isLoading: Boolean = true,
    val isError: Boolean = false,
) : UiState
