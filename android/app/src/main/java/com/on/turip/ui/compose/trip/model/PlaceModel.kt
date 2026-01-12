package com.on.turip.ui.compose.trip.model

import androidx.compose.runtime.Stable

@Stable
data class PlaceModel(
    val id: Long,
    val name: String,
    val isFavorite: Boolean,
    val timeLine: String,
    private val category: String,
    private val mapLink: String,
) {
    val mapModel: MapModel = MapModel.from(mapLink)
    val turipCategory: String = parseCategory()
    val seekTimeSeconds: Int = parseTimeLine()

    private fun parseCategory(): String {
        val findIndex: Int = category.indexOfLast { it == '>' }
        if (findIndex == -1) return category
        return category.substring(findIndex + 1).trim()
    }

    private fun parseTimeLine(): Int {
        val (minute, second) = timeLine.split(":").map { it.toInt() }
        return (minute * 60) + second
    }
}
