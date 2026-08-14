package com.on.turip.core.model.account

data class Account(
    val id: Long,
    val nickname: String,
    val role: Role,
)
