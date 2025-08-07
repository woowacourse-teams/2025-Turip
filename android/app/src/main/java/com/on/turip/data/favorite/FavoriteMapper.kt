package com.on.turip.data.favorite

import com.on.turip.data.content.toDomain
import com.on.turip.data.favorite.dto.FavoriteAddRequest
import com.on.turip.data.favorite.dto.FavoriteContentResponse
import com.on.turip.data.favorite.dto.FavoriteContentsResponse
import com.on.turip.domain.favorite.FavoriteContent
import com.on.turip.domain.favorite.PagedFavoriteContents

fun Long.toRequestDto(): FavoriteAddRequest = FavoriteAddRequest(contentId = this)

fun FavoriteContentsResponse.toDomain(): PagedFavoriteContents =
    PagedFavoriteContents(
        favoriteContents = contents.map { it.toDomain() },
        loadable = loadable,
    )

fun FavoriteContentResponse.toDomain(): FavoriteContent =
    FavoriteContent(
        content = content.toDomain(),
        tripDuration = tripDuration.toDomain(),
        tripPlaceCount = tripPlaceCount,
    )
