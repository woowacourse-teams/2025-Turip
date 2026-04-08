package com.on.turip.ui.common.model.turip

data class TuripEditModel(
    val id: Long,
    val name: String,
    val count: Int,
    val isSelected: Boolean = false,
)
