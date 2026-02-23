package com.on.turip.ui.compose.folder.component

data class MyTuripModel(
    val id: Long,
    val name: String,
    val type: TuripType,
    val isDefault: Boolean,
    val memberCount: Int = 0,
    val placeCount: Int = 0,
)
