package com.on.turip.feature.turipdetail.impl.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.on.turip.feature.turipdetail.impl.model.turip.PlaceLatLngUiModel
import kotlinx.collections.immutable.ImmutableList

@Composable
internal actual fun PlatformTuripMap(
    selectedTuripId: Long,
    selectedPlace: PlaceLatLngUiModel,
    places: ImmutableList<PlaceLatLngUiModel>,
    modifier: Modifier,
) {
    val cameraPositionState = rememberCameraPositionState()

    val markerStates: Map<Long, MarkerState> =
        remember(places) {
            places.associate { place ->
                place.placeId to MarkerState(position = place.toLatLng())
            }
        }

    var isInitialized by remember(selectedTuripId) { mutableStateOf(false) }

    LaunchedEffect(selectedPlace) {
        cameraPositionState.animate(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition(selectedPlace.toLatLng(), 15f, 0f, 0f),
            ),
            durationMs = 1000,
        )
        markerStates[selectedPlace.placeId]?.showInfoWindow()
    }

    LaunchedEffect(places) {
        if (places.isEmpty()) return@LaunchedEffect

        val update =
            when (places.size) {
                1 -> CameraUpdateFactory.newLatLngZoom(places.first().toLatLng(), 15f)
                else -> {
                    val bounds =
                        LatLngBounds
                            .Builder()
                            .apply { places.forEach { include(it.toLatLng()) } }
                            .build()
                    CameraUpdateFactory.newLatLngBounds(bounds, 100)
                }
            }

        if (!isInitialized) {
            cameraPositionState.move(update)
            isInitialized = true
        } else {
            cameraPositionState.animate(update)
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(zoomControlsEnabled = true),
    ) {
        places.forEach { place ->
            Marker(
                state = markerStates[place.placeId] ?: MarkerState(place.toLatLng()),
                title = place.name,
            )
        }
    }
}

private fun PlaceLatLngUiModel.toLatLng(): LatLng = LatLng(latitude, longitude)
