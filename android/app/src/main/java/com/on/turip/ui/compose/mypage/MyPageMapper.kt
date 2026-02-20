package com.on.turip.ui.compose.mypage

import com.on.turip.domain.accounts.Account
import com.on.turip.ui.compose.mypage.model.ProfileModel

fun Account.toUiModel(): ProfileModel =
    ProfileModel(
        id = id,
        nickname = nickname,
        imageUrl = null,
    )
