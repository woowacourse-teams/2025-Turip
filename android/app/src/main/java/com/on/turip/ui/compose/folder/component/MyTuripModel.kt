package com.on.turip.ui.compose.folder.component

import com.on.turip.R

data class MyTuripModel(
    val id: Long,
    val name: String,
    val type: TuripType,
    val isDefault: Boolean,
    val memberCount: Int = 0,
    val placeCount: Int = 0,
) {
    val image: Int
        get() =
            when (type) {
                TuripType.SOLO -> R.drawable.ic_individual_folder
                TuripType.TOGETHER -> R.drawable.ic_together_folder
            }

    companion object {
        val Idle: MyTuripModel =
            MyTuripModel(id = -1L, name = "", type = TuripType.SOLO, isDefault = false)
    }
}
