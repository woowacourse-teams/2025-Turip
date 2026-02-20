package com.on.turip.data.accounts

import com.on.turip.data.accounts.dto.MyProfileResponse
import com.on.turip.domain.accounts.Account
import com.on.turip.domain.accounts.Role

fun MyProfileResponse.toDomain(): Account =
    Account(
        id = id,
        nickname = nickname,
        role = Role.from(role),
    )
