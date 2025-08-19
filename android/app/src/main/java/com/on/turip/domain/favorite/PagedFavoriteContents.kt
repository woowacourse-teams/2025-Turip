package com.on.turip.domain.favorite

data class PagedFavoriteContents(
    val favoriteContents: List<FavoriteContent>,
    val loadable: Boolean,
)
