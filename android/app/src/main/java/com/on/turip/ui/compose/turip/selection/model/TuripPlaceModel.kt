package com.on.turip.ui.compose.turip.selection.model

import androidx.compose.runtime.Stable
import com.google.android.gms.maps.model.LatLng
import com.on.turip.ui.compose.trip.model.MapModel

/**
 * @param turipPlaceId : 튜립 내에서 장소에 대한 id
 * @param placeId : 장소에 대한 고유 id
 */
@Stable
data class TuripPlaceModel(
    val turipPlaceId: Long,
    val placeId: Long,
    val order: Long,
    val name: String,
    val isTuripPlace: Boolean,
    val latLng: LatLng,
    private val category: String,
    private val mapLink: String,
) {
    val mapModel: MapModel = MapModel.from(mapLink)

    val turipCategory: String
        get() {
            val findIndex: Int = category.indexOfLast { it == '>' }
            if (findIndex == -1) return category
            return category.substring(findIndex + 1).trim()
        }

    companion object {
        val Idle: TuripPlaceModel =
            TuripPlaceModel(
                turipPlaceId = 0L,
                placeId = 0L,
                order = 0L,
                name = "",
                isTuripPlace = false,
                latLng = LatLng(0.0, 0.0),
                category = "",
                mapLink = "",
            )
    }
}
