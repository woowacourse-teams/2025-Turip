package com.on.turip.feature.mypage.impl.util

import com.on.turip.core.model.account.Account
import com.on.turip.feature.mypage.impl.model.ProfileModel

fun Account.toUiModel(): ProfileModel =
    ProfileModel(
        id = id,
        nickname = nickname,
        imageUrl = null,
    )
