package com.on.turip.feature.trip.impl.model

import androidx.compose.runtime.Composable
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_trip_day_trip_duration
import com.on.turip.core.designsystem.generated.resources.all_trip_duration
import org.jetbrains.compose.resources.stringResource

data class TripDurationModel(
    val nights: Int,
    val days: Int,
) {
    private val isDayTrip: Boolean = nights == 0 && days == 1

    @Composable
    fun toDisplayText(): String =
        if (isDayTrip) {
            stringResource(Res.string.all_trip_day_trip_duration)
        } else {
            stringResource(Res.string.all_trip_duration, nights, days)
        }
}
