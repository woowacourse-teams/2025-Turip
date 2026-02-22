package com.on.turip.ui.compose.folder.mapper

import com.on.turip.domain.turip.Turip
import com.on.turip.ui.compose.folder.component.MyTuripModel
import com.on.turip.ui.compose.folder.component.TuripType

fun Turip.toUiModel(): MyTuripModel =
    MyTuripModel(
        id = id,
        name = name,
        placeCount = placeCount,
        memberCount = memberCount,
        type = TuripType.of(isShared),
    )
