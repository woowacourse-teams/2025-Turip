package com.on.turip.ui.folder.model

data class FolderEditModel(
    val id: Long,
    val name: String,
    val count: Int,
    val isSelected: Boolean = false,
)
