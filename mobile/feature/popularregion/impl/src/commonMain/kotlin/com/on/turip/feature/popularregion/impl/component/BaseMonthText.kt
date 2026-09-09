package com.on.turip.feature.popularregion.impl.component

import androidx.compose.runtime.Composable
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.popular_region_base_month
import com.on.turip.core.designsystem.generated.resources.popular_region_recent_month
import com.on.turip.core.ui.util.formatResource
import org.jetbrains.compose.resources.stringResource

/**
 * 방문자 수 기준월을 화면 문구로 만든다.
 *
 * 서버에 수집된 데이터가 없어 기준월이 없을 때도 `최근 한 달`로 말은 되게 한다.
 * 앱 바와 지도 배지가 같은 문구를 써야 해서 한곳에 둔다.
 */
@Composable
internal fun baseMonthText(
    year: Int?,
    monthOfYear: Int?,
): String =
    if (year != null && monthOfYear != null) {
        stringResource(Res.string.popular_region_base_month).formatResource(year, monthOfYear)
    } else {
        stringResource(Res.string.popular_region_recent_month)
    }
