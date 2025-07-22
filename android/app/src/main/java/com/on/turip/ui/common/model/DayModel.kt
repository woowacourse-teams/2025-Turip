package com.on.turip.ui.common.model

data class DayModel(
    val day: Int,
    val isSelected: Boolean = false,
) {
    fun isSame(dayModel: DayModel): Boolean = this == dayModel
}

fun Int.initDayModels(): List<DayModel> = (1..this).map { it.initDayModel() }

private fun Int.initDayModel(): DayModel =
    when (this) {
        1 -> DayModel(1, true)
        else -> DayModel(this, false)
    }
