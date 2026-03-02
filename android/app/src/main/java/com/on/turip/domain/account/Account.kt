package com.on.turip.domain.account

data class Account(
    val id: Long,
    val nickname: String,
    val role: Role,
)
