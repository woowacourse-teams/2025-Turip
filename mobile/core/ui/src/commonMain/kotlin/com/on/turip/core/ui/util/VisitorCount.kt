package com.on.turip.core.ui.util

import kotlin.math.round

/**
 * 방문자 수를 `2847만`, `480.5만` 처럼 만 단위로 줄여 적는다.
 *
 * 지도 시트와 지역 브리핑이 같은 숫자를 같은 모양으로 보여야 해서 한곳에 둔다.
 */
fun Long.toVisitorCountText(): String {
    val man: Double = this / MAN
    val rounded: Double = round(man * 10) / 10
    val hasFraction: Boolean = rounded != round(rounded)
    return if (hasFraction) "${rounded}만" else "${rounded.toLong()}만"
}

private const val MAN: Double = 10_000.0
