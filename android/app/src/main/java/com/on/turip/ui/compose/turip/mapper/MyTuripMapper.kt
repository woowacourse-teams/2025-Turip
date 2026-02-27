package com.on.turip.ui.compose.turip.mapper

import com.on.turip.domain.turip.Turip
import com.on.turip.ui.compose.turip.model.MyTuripModel
import com.on.turip.ui.compose.turip.model.TuripTypeModel
import com.on.turip.ui.folder.model.TuripEditModel

fun Turip.toUiMyTuripModel(): MyTuripModel =
    MyTuripModel(
        id = id,
        name = name,
        placeCount = placeCount,
        memberCount = memberCount,
        type = TuripTypeModel.of(isShared),
        isDefault = isDefault,
    )

fun MyTuripModel.toEditModel(): TuripEditModel =
    TuripEditModel(
        id = id,
        name = name,
        count = placeCount,
    )
