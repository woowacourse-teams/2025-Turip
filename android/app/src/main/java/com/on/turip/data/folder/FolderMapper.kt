package com.on.turip.data.folder

import com.on.turip.data.folder.dto.FavoriteFolderAddRequest
import com.on.turip.data.folder.dto.FavoriteFolderPatchRequest
import com.on.turip.data.folder.dto.FavoriteFolderResponse
import com.on.turip.data.folder.dto.FavoriteFoldersResponse
import com.on.turip.domain.folder.Folder

fun FavoriteFoldersResponse.toDomain(): List<Folder> = favoriteFolderResponses.map { it.toDomain() }

fun FavoriteFolderResponse.toDomain(): Folder =
    Folder(
        id = id,
        name = name,
        isDefault = isDefault,
        placeCount = placeCount,
    )

fun String.toAddRequestDto(): FavoriteFolderAddRequest = FavoriteFolderAddRequest(name = this)

fun String.toPatchRequestDto(): FavoriteFolderPatchRequest = FavoriteFolderPatchRequest(name = this)
