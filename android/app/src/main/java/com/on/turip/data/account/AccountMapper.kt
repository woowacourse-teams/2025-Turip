package com.on.turip.data.account

import com.on.turip.data.account.dto.MyProfileResponse
import com.on.turip.domain.account.Account
import com.on.turip.domain.account.Role

fun MyProfileResponse.toDomain(): Account =
    Account(
        id = id,
        nickname = nickname,
        role = Role.from(role),
    )
