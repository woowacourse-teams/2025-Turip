package com.on.turip.feature.trip.impl.model

import androidx.compose.runtime.Stable
import com.on.turip.core.ui.model.AppleMap

@Stable
data class PlaceModel(
    val id: Long,
    val name: String,
    val isTuripPlace: Boolean,
    val timeLine: String,
    private val category: String,
    private val mapLink: String,
    private val appleMap: AppleMap,
) {
    val mapModel: MapModel =
        MapModel.from(
            url = mapLink,
            appleMap = appleMap,
        )
    val turipCategory: String
        get() {
            val findIndex: Int = category.indexOfLast { it == '>' }
            if (findIndex == -1) return category
            return category.substring(findIndex + 1).trim()
        }
    val seekTimeSeconds: Int
        get() {
            val (minute, second) = timeLine.split(":").map { it.toInt() }
            return (minute * 60) + second
        }
}
