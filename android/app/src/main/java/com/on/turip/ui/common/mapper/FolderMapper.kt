package com.on.turip.ui.common.mapper

import com.on.turip.domain.folder.Turip
import com.on.turip.ui.folder.model.TuripEditModel
import com.on.turip.ui.main.favorite.model.TuripModel

fun Turip.toUiModel(selectTuripId: Long): TuripModel =
    TuripModel(
        id = id,
        name = name,
        placeCount = placeCount,
        isSelected = id == selectTuripId,
    )

fun Turip.toEditUiModel(): TuripEditModel =
    TuripEditModel(
        id = id,
        name = name,
        count = placeCount,
        isSelected = false,
    )
