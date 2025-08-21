package com.on.turip.domain.folder

class FavoriteFolder(
    val id: Long,
    val name: String,
    val isDefault: Boolean,
    val isFavorite: Boolean,
    val placeCount: Int = 0,
)
