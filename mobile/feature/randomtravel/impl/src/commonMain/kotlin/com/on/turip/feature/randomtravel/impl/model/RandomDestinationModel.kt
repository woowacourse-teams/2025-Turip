package com.on.turip.feature.randomtravel.impl.model

import androidx.compose.runtime.Immutable

/**
 * 슬롯에서 확정된 여행지. 티켓에 그대로 출력된다.
 *
 * Phase 1에서 채울 수 있는 필드만 가진다.
 * (카테고리 / 예상 이동시간 / 날씨는 서버 데이터가 준비된 뒤 Phase 2에서 추가한다)
 */
@Immutable
data class RandomDestinationModel(
    val name: String,
    val imageUrl: String,
    val isDomestic: Boolean,
    val videoCount: Int,
)
