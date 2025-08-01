package com.on.turip.ui.common.mapper

import com.on.turip.domain.content.video.VideoInformation
import com.on.turip.ui.search.model.VideoInformationModel

fun VideoInformation.toUiModel(): VideoInformationModel =
    VideoInformationModel(
        content = content,
        tripModel = trip.toUiModel(),
    )
