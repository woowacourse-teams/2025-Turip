package com.on.turip.ui.compose.turipdetail.component

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.on.turip.ui.compose.turipdetail.model.turip.PlaceLatLngUiModel
import kotlinx.collections.immutable.ImmutableList

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun TuripMapContent(
    selectedTuripId: Long,
    places: ImmutableList<PlaceLatLngUiModel>,
    selectedPlace: PlaceLatLngUiModel,
    isMapVisible: Boolean,
    onMapToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cameraPositionState = rememberCameraPositionState()

    val markerStates: Map<Long, MarkerState> =
        remember(places) {
            places.associate { it.placeId to MarkerState(position = it.latLng) }
        }

    var isInitialized by remember(selectedTuripId) { mutableStateOf(false) }

    LaunchedEffect(selectedPlace) {
        cameraPositionState.animate(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition(selectedPlace.latLng, 15f, 0f, 0f),
            ),
            durationMs = 1000,
        )
        markerStates[selectedPlace.placeId]?.showInfoWindow()
    }

    LaunchedEffect(places) {
        if (places.isEmpty()) return@LaunchedEffect

        val update =
            when (places.size) {
                1 -> {
                    CameraUpdateFactory.newLatLngZoom(places.first().latLng, 15f)
                }

                else -> {
                    val bounds =
                        LatLngBounds
                            .Builder()
                            .apply { places.forEach { include(it.latLng) } }
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

    val mapHeight by animateDpAsState(
        targetValue = if (isMapVisible) 260.dp else 0.dp,
        animationSpec = tween(250),
    )

    Column(
        modifier =
            modifier
                .clipToBounds(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(mapHeight),
        ) {
            if (isMapVisible) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(zoomControlsEnabled = true),
                ) {
                    places.forEach { place ->
                        Marker(
                            state = markerStates[place.placeId] ?: MarkerState(place.latLng),
                            title = place.name,
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            IconButton(
                onClick = onMapToggle,
                modifier = Modifier.align(Alignment.Center),
            ) {
                Icon(
                    imageVector = if (isMapVisible) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                )
            }
        }
    }
}
