package com.on.turip.core.data.mapper

import com.on.turip.core.data.dto.account.MyProfileResponse
import com.on.turip.core.model.account.Account
import com.on.turip.core.model.account.Role

fun MyProfileResponse.toDomain(): Account =
    Account(
        id = id,
        nickname = nickname,
        role = Role.from(role),
    )
