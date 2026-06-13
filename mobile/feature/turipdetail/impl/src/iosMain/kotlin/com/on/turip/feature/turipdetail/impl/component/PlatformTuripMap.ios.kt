package com.on.turip.feature.turipdetail.impl.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import com.on.turip.core.ui.platform.IosGoogleMapBridge
import com.on.turip.feature.turipdetail.impl.model.turip.PlaceLatLngUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import kotlinx.cinterop.readValue
import platform.CoreGraphics.CGRectZero
import platform.CoreLocation.CLLocationCoordinate2D
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation
import platform.UIKit.UIView

@OptIn(ExperimentalForeignApi::class)
@Composable
internal actual fun PlatformTuripMap(
    selectedTuripId: Long,
    selectedPlace: PlaceLatLngUiModel,
    places: ImmutableList<PlaceLatLngUiModel>,
    modifier: Modifier,
) {
    val fallbackMapView = remember(selectedTuripId) { FallbackMapViewState() }

    UIKitView(
        factory = {
            IosGoogleMapBridge.createView()
                ?: fallbackMapView.createView()
        },
        modifier = modifier,
        update = { mapView ->
            val targetPlace = selectedPlace.validOrNull() ?: places.firstValidOrNull() ?: return@UIKitView
            IosGoogleMapBridge.updateView(
                view = mapView,
                selectedTuripId = selectedTuripId,
                selectedLatitude = targetPlace.latitude,
                selectedLongitude = targetPlace.longitude,
                selectedName = targetPlace.name,
                placesPayload = places.toPayload(),
            )

            if (mapView is MKMapView) {
                fallbackMapView.updateView(mapView, targetPlace, places)
            }
        },
    )
}

private fun PlaceLatLngUiModel.validOrNull(): PlaceLatLngUiModel? =
    takeUnless { latitude == 0.0 && longitude == 0.0 }

private fun List<PlaceLatLngUiModel>.firstValidOrNull(): PlaceLatLngUiModel? =
    firstOrNull { it.latitude != 0.0 || it.longitude != 0.0 }

private fun List<PlaceLatLngUiModel>.toPayload(): String =
    filter { it.latitude != 0.0 || it.longitude != 0.0 }
        .joinToString(separator = "\n") { place ->
            listOf(
                place.placeId.toString(),
                place.latitude.toString(),
                place.longitude.toString(),
                place.name.sanitizePayloadField(),
            ).joinToString(separator = "\t")
        }

private fun String.sanitizePayloadField(): String =
    replace("\t", " ")
        .replace("\n", " ")

@OptIn(ExperimentalForeignApi::class)
private class FallbackMapViewState {
    fun createView(): UIView =
        MKMapView(frame = CGRectZero.readValue()).apply {
            scrollEnabled = true
            zoomEnabled = true
            rotateEnabled = false
        }

    fun updateView(
        mapView: MKMapView,
        selectedPlace: PlaceLatLngUiModel,
        places: List<PlaceLatLngUiModel>,
    ) {
        mapView.removeAnnotations(mapView.annotations)

        places
            .filter { it.latitude != 0.0 || it.longitude != 0.0 }
            .forEach { place ->
                val annotation =
                    MKPointAnnotation().apply {
                        setCoordinate(coordinate(place.latitude, place.longitude))
                        setTitle(place.name)
                    }
                mapView.addAnnotation(annotation)
            }

        mapView.setRegion(
            MKCoordinateRegionMakeWithDistance(
                coordinate(selectedPlace.latitude, selectedPlace.longitude),
                1_000.0,
                1_000.0,
            ),
            animated = true,
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun coordinate(
    latitude: Double,
    longitude: Double,
): CValue<CLLocationCoordinate2D> =
    cValue {
        this.latitude = latitude
        this.longitude = longitude
    }
