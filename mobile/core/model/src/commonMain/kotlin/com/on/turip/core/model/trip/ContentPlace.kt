package com.on.turip.core.model.trip

data class ContentPlace(
    val tripCourseId: Long,
    val visitDay: Int,
    val visitOrder: Int,
    val place: Place,
    val timeLine: String,
    val isTuripPlace: Boolean,
)
