package com.on.turip.ui.compose.turip.mapper

import com.on.turip.domain.turip.Turip
import com.on.turip.domain.turip.TuripType
import com.on.turip.ui.common.model.turip.TuripEditModel
import com.on.turip.ui.compose.turip.model.MyTuripModel

fun Turip.toUiMyTuripModel(): MyTuripModel =
    MyTuripModel(
        id = id,
        name = name,
        placeCount = placeCount,
        memberCount = memberCount,
        type = TuripType.from(isShared),
        isDefault = isDefault,
    )

fun MyTuripModel.toEditModel(): TuripEditModel =
    TuripEditModel(
        id = id,
        name = name,
        count = placeCount,
    )
