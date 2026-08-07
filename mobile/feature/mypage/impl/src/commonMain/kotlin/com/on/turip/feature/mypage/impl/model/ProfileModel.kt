package com.on.turip.feature.mypage.impl.model

import androidx.compose.runtime.Immutable

@Immutable
data class ProfileModel(
    val id: Long,
    val nickname: String,
    val imageUrl: String?,
)
