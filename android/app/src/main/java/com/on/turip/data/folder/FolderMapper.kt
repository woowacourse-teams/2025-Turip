package com.on.turip.data.folder

import com.on.turip.data.folder.dto.FavoriteFolderAddRequest
import com.on.turip.data.folder.dto.FavoriteFolderPatchRequest
import com.on.turip.data.folder.dto.FavoriteFolderResponse
import com.on.turip.data.folder.dto.FavoriteFolderStatusByPlaceResponse
import com.on.turip.data.folder.dto.FavoriteFoldersResponse
import com.on.turip.data.folder.dto.FavoriteFoldersStatusByPlaceResponse
import com.on.turip.domain.folder.FavoriteFolder
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

fun FavoriteFoldersStatusByPlaceResponse.toDomain(): List<FavoriteFolder> = favoriteFolders.map { it.toDomain() }

fun FavoriteFolderStatusByPlaceResponse.toDomain(): FavoriteFolder =
    FavoriteFolder(
        id = id,
        name = name,
        isDefault = isDefault,
        isFavorite = isFavoritePlace,
    )
