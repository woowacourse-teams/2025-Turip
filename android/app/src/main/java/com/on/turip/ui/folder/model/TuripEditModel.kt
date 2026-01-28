package com.on.turip.ui.folder.model

data class TuripEditModel(
    val id: Long,
    val name: String,
    val count: Int,
    val isSelected: Boolean = false,
)
