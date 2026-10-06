package com.on.turip.feature.randomtravel.impl.model

import androidx.compose.runtime.Immutable

/**
 * `이 여행 시작하기` 확인 시트에서 새 튜립에 담을 후보로 보여주는 장소.
 *
 * @param isSelected 기본값은 선택됨. 사용자가 빼고 싶은 곳만 해제한다.
 */
@Immutable
data class TuripDraftPlaceModel(
    val placeId: Long,
    val name: String,
    val category: String,
    val isSelected: Boolean = true,
)
