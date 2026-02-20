package com.on.turip.domain.accounts

data class Account(
    val id: Long,
    val nickname: String,
    val role: Role,
)
