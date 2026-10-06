package com.on.turip.core.data.dto.turip

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 장소 일괄 담기 응답의 한 건. 이미 담겨 있던 장소는 응답에서 빠지기 때문에
 * 요청한 수보다 적게 내려올 수 있다.
 */
@Serializable
data class TuripPlaceCreationResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("turipId")
    val turipId: Long,
    @SerialName("placeId")
    val placeId: Long,
)
