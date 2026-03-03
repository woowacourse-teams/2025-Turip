package com.on.turip.ui.compose.mypage.model

import androidx.compose.runtime.Immutable

@Immutable
data class ProfileModel(
    val id: Long,
    val nickname: String,
    val imageUrl: String?,
)
