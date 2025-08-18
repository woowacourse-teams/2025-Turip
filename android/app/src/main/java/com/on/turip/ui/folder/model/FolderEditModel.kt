package com.on.turip.ui.folder.model

data class FolderEditModel(
    val id: Long = 0L,
    val name: String,
    val count: Int = 0,
    val isSelected: Boolean = false,
)
