package com.on.turip.ui.compose.turip.model

import com.on.turip.R

data class MyTuripModel(
    val id: Long,
    val name: String,
    val type: TuripTypeModel,
    val isDefault: Boolean,
    val memberCount: Int = 0,
    val placeCount: Int = 0,
) {
    val image: Int
        get() =
            when (type) {
                TuripTypeModel.SOLO -> R.drawable.ic_individual_folder
                TuripTypeModel.TOGETHER -> R.drawable.ic_together_folder
            }

    companion object {
        val Idle: MyTuripModel =
            MyTuripModel(id = -1L, name = "", type = TuripTypeModel.SOLO, isDefault = false)
    }
}
