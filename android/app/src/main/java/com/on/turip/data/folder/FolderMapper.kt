package com.on.turip.data.folder

import com.on.turip.data.folder.dto.FavoriteFolderPatchRequest
import com.on.turip.data.folder.dto.FavoriteFolderPostRequest
import com.on.turip.data.folder.dto.FavoriteFolderPostResponse
import com.on.turip.data.folder.dto.FavoriteFolderStatusByPlaceResponse
import com.on.turip.data.folder.dto.FavoriteFoldersResponse
import com.on.turip.data.folder.dto.FavoriteFoldersStatusByPlaceResponse
import com.on.turip.domain.folder.FavoriteFolder
import com.on.turip.domain.folder.Folder

fun FavoriteFoldersResponse.toDomain(): List<Folder> = favoriteFolderResponses.map { it.toDomain() }

fun FavoriteFolderPostResponse.toDomain(): Folder =
    Folder(
        id = id,
        name = name,
        isDefault = isDefault,
        placeCount = 0,
    )

fun String.toPostRequestDto(): FavoriteFolderPostRequest = FavoriteFolderPostRequest(name = this)

fun String.toPatchRequestDto(): FavoriteFolderPatchRequest = FavoriteFolderPatchRequest(name = this)

fun FavoriteFoldersStatusByPlaceResponse.toDomain(): List<FavoriteFolder> = favoriteFolders.map { it.toDomain() }

fun FavoriteFolderStatusByPlaceResponse.toDomain(): FavoriteFolder =
    FavoriteFolder(
        id = id,
        name = name,
        isDefault = isDefault,
        isFavorite = isFavoritePlace,
    )
