package com.on.turip.feature.search.impl.model

data class TripDurationModel(
    val nights: Int,
    val days: Int,
) {
    fun toDisplayText(): String =
        if (nights == 0 && days == 1) {
            "당일치기"
        } else {
            "${nights}박 ${days}일"
        }
}
