package com.on.turip.ui.compose.mypage.util

import com.on.turip.domain.account.Account
import com.on.turip.ui.compose.mypage.model.ProfileModel

fun Account.toUiModel(): ProfileModel =
    ProfileModel(
        id = id,
        nickname = nickname,
        imageUrl = null,
    )
