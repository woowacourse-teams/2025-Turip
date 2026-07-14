package com.on.turip.core.ui.model.turip

data class TuripEditModel(
    val id: Long,
    val name: String,
    val count: Int,
    val isSelected: Boolean = false,
)
