package com.on.turip.ui.common.model.trip

import android.content.Context
import com.on.turip.R

data class TripDurationModel(
    val nights: Int,
    val days: Int,
) {
    fun isDayTrip(): Boolean = nights == 0 && days == 1
}

fun TripDurationModel.toDisplayText(context: Context): String =
    if (isDayTrip()) {
        context.getString(R.string.all_travel_day_trip_duration)
    } else {
        context.getString(R.string.all_travel_duration, nights, days)
    }
