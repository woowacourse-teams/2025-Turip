package com.on.turip.ui.trip.detail

data class DayModel(
    val day: Int,
    val isSelected: Boolean = false,
) {
    companion object {
        const val ALL_PLACE = 0
    }
}
