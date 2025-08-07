package com.on.turip.data.favorite

import com.on.turip.data.favorite.dto.FavoriteAddRequest

fun Long.toRequestDto(): FavoriteAddRequest = FavoriteAddRequest(contentId = this)
