package com.on.turip.core.ui.util

import androidx.compose.runtime.Composable
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.popular_region_base_month
import com.on.turip.core.designsystem.generated.resources.popular_region_recent_month
import org.jetbrains.compose.resources.stringResource

/**
 * 방문자 수 기준월을 화면 문구로 만든다.
 *
 * 서버에 수집된 데이터가 없어 기준월이 없을 때도 `최근 한 달`로 말은 되게 한다.
 * 지도(앱 바·배지·시트)와 지역 브리핑이 같은 문구를 써야 해서 한곳에 둔다.
 */
@Composable
fun baseMonthText(
    year: Int?,
    monthOfYear: Int?,
): String =
    if (year != null && monthOfYear != null) {
        stringResource(Res.string.popular_region_base_month).formatResource(year, monthOfYear)
    } else {
        stringResource(Res.string.popular_region_recent_month)
    }

/** `202506` 형식을 그대로 받는 쪽. 형식이 다르면 연·월이 없는 것으로 본다. */
@Composable
fun baseMonthText(baseMonth: String?): String =
    baseMonthText(year = baseMonth.toBaseYear(), monthOfYear = baseMonth.toBaseMonthOfYear())

/** 기준월을 화면에 적기 위해 `202506` 을 연/월로 나눈다. 형식이 다르면 둘 다 null 이다. */
fun String?.toBaseYear(): Int? =
    this?.takeIf { it.length == BASE_MONTH_LENGTH }?.take(YEAR_LENGTH)?.toIntOrNull()

fun String?.toBaseMonthOfYear(): Int? =
    this
        ?.takeIf { it.length == BASE_MONTH_LENGTH }
        ?.drop(YEAR_LENGTH)
        ?.toIntOrNull()
        ?.takeIf { it in MONTH_RANGE }

private const val BASE_MONTH_LENGTH: Int = 6
private const val YEAR_LENGTH: Int = 4
private val MONTH_RANGE: IntRange = 1..12
