package com.on.turip.feature.turipdetail.impl.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import com.on.turip.feature.turipdetail.impl.model.turip.PlaceLatLngUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKCoordinateRegionMake
import platform.MapKit.MKCoordinateSpanMake
import platform.MapKit.MKMapTypeStandard
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation

@OptIn(ExperimentalForeignApi::class)
@Composable
internal actual fun PlatformTuripMap(
    selectedTuripId: Long,
    selectedPlace: PlaceLatLngUiModel,
    places: ImmutableList<PlaceLatLngUiModel>,
    modifier: Modifier,
) {
    UIKitView(
        factory = {
            MKMapView().apply {
                mapType = MKMapTypeStandard
                zoomEnabled = true
                scrollEnabled = true
                rotateEnabled = false
                pitchEnabled = false
                showsCompass = true
            }
        },
        modifier = modifier,
        update = { mapView ->
            mapView.renderPlaces(
                selectedPlace = selectedPlace,
                places = places,
            )
        },
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun MKMapView.renderPlaces(
    selectedPlace: PlaceLatLngUiModel,
    places: ImmutableList<PlaceLatLngUiModel>,
) {
    removeAnnotations(annotations)

    val visiblePlaces = places.filterNot { it.isEmptyCoordinate() }
    if (visiblePlaces.isEmpty()) return

    addAnnotations(
        visiblePlaces.map { place ->
            MKPointAnnotation().apply {
                setCoordinate(CLLocationCoordinate2DMake(place.latitude, place.longitude))
                setTitle(place.name)
            }
        },
    )

    val targetPlace =
        selectedPlace
            .takeUnless { it.isEmptyCoordinate() }
            ?: visiblePlaces.first()

    setRegion(
        targetPlace.regionFor(visiblePlaces),
        animated = true,
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun PlaceLatLngUiModel.regionFor(places: List<PlaceLatLngUiModel>) =
    if (places.size == 1) {
        MKCoordinateRegionMake(
            centerCoordinate = CLLocationCoordinate2DMake(latitude, longitude),
            span = MKCoordinateSpanMake(DEFAULT_LATITUDE_DELTA, DEFAULT_LONGITUDE_DELTA),
        )
    } else {
        val minLatitude = places.minOf { it.latitude }
        val maxLatitude = places.maxOf { it.latitude }
        val minLongitude = places.minOf { it.longitude }
        val maxLongitude = places.maxOf { it.longitude }
        val latitudeDelta = ((maxLatitude - minLatitude) * MAP_PADDING_RATIO).coerceAtLeast(DEFAULT_LATITUDE_DELTA)
        val longitudeDelta = ((maxLongitude - minLongitude) * MAP_PADDING_RATIO).coerceAtLeast(DEFAULT_LONGITUDE_DELTA)

        MKCoordinateRegionMake(
            centerCoordinate = CLLocationCoordinate2DMake(
                latitude = (minLatitude + maxLatitude) / 2.0,
                longitude = (minLongitude + maxLongitude) / 2.0,
            ),
            span = MKCoordinateSpanMake(latitudeDelta, longitudeDelta),
        )
    }

private fun PlaceLatLngUiModel.isEmptyCoordinate(): Boolean = latitude == 0.0 && longitude == 0.0

private const val DEFAULT_LATITUDE_DELTA = 0.01
private const val DEFAULT_LONGITUDE_DELTA = 0.01
private const val MAP_PADDING_RATIO = 1.4
