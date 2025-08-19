package com.on.turip.domain.folder

data class Folder(
    val id: Long,
    val name: String,
    val isDefault: Boolean,
    val placeCount: Int,
)
