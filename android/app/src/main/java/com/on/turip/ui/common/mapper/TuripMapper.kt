package com.on.turip.ui.common.mapper

import com.on.turip.domain.turip.Turip
import com.on.turip.ui.folder.model.TuripEditModel
import com.on.turip.ui.main.favorite.model.TuripModel

fun Turip.toUiModel(selectTuripId: Long): TuripModel =
    TuripModel(
        id = id,
        name = name,
        placeCount = placeCount,
        isSelected = id == selectTuripId,
        isDefault = isDefault,
    )

fun Turip.toEditUiModel(): TuripEditModel =
    TuripEditModel(
        id = id,
        name = name,
        count = placeCount,
        isSelected = false,
    )
