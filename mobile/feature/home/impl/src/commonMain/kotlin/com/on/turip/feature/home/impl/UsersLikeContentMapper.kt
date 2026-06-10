package com.on.turip.feature.home.impl

import com.on.turip.core.model.content.UsersLikeContent
import com.on.turip.feature.home.impl.model.UsersLikeContentModel

fun UsersLikeContent.toUiModel(): UsersLikeContentModel =
    UsersLikeContentModel(
        content = content,
        tripDuration = tripDuration,
    )
