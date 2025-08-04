package com.on.turip.data.favorite

import com.on.turip.data.favorite.dto.FavoriteAddRequest

fun Long.toFavoriteAddRequest(): FavoriteAddRequest = FavoriteAddRequest(contentId = this)
