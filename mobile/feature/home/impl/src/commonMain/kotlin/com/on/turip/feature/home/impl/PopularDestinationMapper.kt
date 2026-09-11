package com.on.turip.feature.home.impl

import com.on.turip.core.model.region.DestinationVisitor
import com.on.turip.feature.home.impl.model.PopularDestinationModel
import kotlin.math.round

fun DestinationVisitor.toUiModel(): PopularDestinationModel =
    PopularDestinationModel(
        rank = rank,
        regionCategoryName = regionCategoryName,
        visitorCountText = visitorCount.toManUnitText(),
    )

private const val MAN: Double = 10_000.0

/** 인기 관광지 지도의 시트와 같은 표기를 쓴다. 두 화면에서 같은 숫자가 다르게 보이면 안 된다. */
private fun Long.toManUnitText(): String {
    val man: Double = this / MAN
    val rounded: Double = round(man * 10) / 10
    val hasFraction: Boolean = rounded != round(rounded)
    return if (hasFraction) "${rounded}만" else "${rounded.toLong()}만"
}
