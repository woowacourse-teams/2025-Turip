package com.on.turip.ui.common.mapper

import com.on.turip.domain.folder.Folder
import com.on.turip.ui.folder.model.FolderEditModel
import com.on.turip.ui.main.favorite.model.TuripModel

fun Folder.toUiModel(selectFolderId: Long): TuripModel =
    TuripModel(
        id = id,
        name = name,
        placeCount = placeCount,
        isSelected = id == selectFolderId,
    )

fun Folder.toEditUiModel(): FolderEditModel =
    FolderEditModel(
        id = id,
        name = name,
        count = placeCount,
        isSelected = false,
    )
