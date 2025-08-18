package com.on.turip.ui.trip.detail

import android.net.Uri
import androidx.core.net.toUri

data class PlaceModel(
    val name: String,
    val category: String,
    val mapLink: String,
    val timeLine: String,
) {
    val placeUri: Uri
        get() = mapLink.toUri()
    val turipCategory: String
        get() = parseCategory()
    val contentTimeLine: Int
        get() = parseTimeLine()

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
