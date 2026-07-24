package com.on.turip.feature.turip.impl.mapper

import com.on.turip.core.model.turip.Turip
import com.on.turip.core.model.turip.TuripType
import com.on.turip.core.ui.model.turip.TuripEditModel
import com.on.turip.feature.turip.impl.model.MyTuripModel

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
