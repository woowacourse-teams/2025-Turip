package com.on.turip.ui.main.favorite.model

data class PlaceTuripSelectionUiState(
    val placeId: Long,
    val turips: List<TuripModel>,
) {
    val isIncludedInAnyTurip: Boolean
        get() = turips.any { it.isSelected }

    companion object {
        val Idle: PlaceTuripSelectionUiState =
            PlaceTuripSelectionUiState(
                placeId = 0L,
                turips = emptyList(),
            )
    }
}
