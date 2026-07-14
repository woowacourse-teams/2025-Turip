package com.on.turip.feature.mypage.impl

import androidx.compose.runtime.Immutable

@Immutable
sealed interface MyPageSectionState<out T> {
    data object Loading : MyPageSectionState<Nothing>

    @Immutable
    data class Success<T>(
        val data: T,
    ) : MyPageSectionState<T>

    data object Error : MyPageSectionState<Nothing>
}
