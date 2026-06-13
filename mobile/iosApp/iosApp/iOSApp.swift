import SwiftUI
import ComposeApp
import FirebaseCore
import FirebaseInstallations
import GoogleMaps
import QuartzCore

@main
struct iOSApp: App {
    init() {
        configureGoogleMaps()
        KoinIosKt.startKoinIos()
        configureFirebaseInstallationId()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    IosDeepLinkBridgeKt.emitIosDeepLink(url: url.absoluteString)
                }
                .onContinueUserActivity(NSUserActivityTypeBrowsingWeb) { activity in
                    if let url = activity.webpageURL {
                        IosDeepLinkBridgeKt.emitIosDeepLink(url: url.absoluteString)
                    }
                }
        }
    }

    private func configureGoogleMaps() {
        let apiKey = MainViewControllerKt.googleMapsApiKey()
        guard !apiKey.isEmpty else { return }

        GMSServices.provideAPIKey(apiKey)
        MainViewControllerKt.registerIosGoogleMapView(
            factory: {
                let mapView = TuripGoogleMapView(frame: .zero)
                mapView.configure()
                return mapView
            },
            updater: { view, selectedTuripId, selectedLatitude, selectedLongitude, selectedName, placesPayload in
                guard let mapView = view as? TuripGoogleMapView else { return }
                mapView.update(
                    selectedTuripId: selectedTuripId.int64Value,
                    selectedLatitude: selectedLatitude.doubleValue,
                    selectedLongitude: selectedLongitude.doubleValue,
                    selectedName: selectedName,
                    placesPayload: placesPayload
                )
            }
        )
    }

    private func configureFirebaseInstallationId() {
        guard Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist") != nil else {
            IosFirebaseInstallationBridgeKt.provideFirebaseInstallationId(fid: nil)
            return
        }

        if FirebaseApp.app() == nil {
            FirebaseApp.configure()
        }

        Installations.installations().installationID { installationID, _ in
            IosFirebaseInstallationBridgeKt.provideFirebaseInstallationId(fid: installationID)
        }
    }
}

private final class TuripGoogleMapView: GMSMapView {
    private let defaultZoom: Float = 15
    private let selectedPlaceAnimationDuration: CFTimeInterval = 1
    private let boundsPadding: CGFloat = 100
    private var currentSelectedTuripId: Int64?
    private var isInitialized = false
    private var previousSelectedCoordinate: CLLocationCoordinate2D?
    private var previousPlacesPayload = ""

    func configure() {
        settings.zoomGestures = true
        settings.scrollGestures = true
        settings.rotateGestures = false
        settings.tiltGestures = false
    }

    func update(
        selectedTuripId: Int64,
        selectedLatitude: Double,
        selectedLongitude: Double,
        selectedName: String,
        placesPayload: String
    ) {
        let selectedCoordinate = CLLocationCoordinate2D(
            latitude: selectedLatitude,
            longitude: selectedLongitude
        )
        let selectedCoordinateOrNil = selectedCoordinate.validOrNil
        let places = parsePlaces(placesPayload)

        if currentSelectedTuripId != selectedTuripId {
            currentSelectedTuripId = selectedTuripId
            isInitialized = false
            previousSelectedCoordinate = nil
            previousPlacesPayload = ""
        }

        clear()

        var selectedMarker: GMSMarker?
        places.forEach { place in
            let marker = GMSMarker()
            marker.position = CLLocationCoordinate2D(latitude: place.latitude, longitude: place.longitude)
            marker.title = place.name
            marker.map = self

            if let selectedCoordinateOrNil,
               marker.position.isSameCoordinate(as: selectedCoordinateOrNil) {
                selectedMarker = marker
            }
        }

        if let selectedMarker {
            self.selectedMarker = selectedMarker
        } else if let selectedCoordinateOrNil, !selectedName.isEmpty {
            let marker = GMSMarker(position: selectedCoordinate)
            marker.title = selectedName
            marker.map = self
            self.selectedMarker = marker
        }

        updateCamera(
            selectedCoordinate: selectedCoordinateOrNil,
            places: places,
            placesPayload: placesPayload
        )
        previousSelectedCoordinate = selectedCoordinateOrNil
        previousPlacesPayload = placesPayload
    }

    private func updateCamera(
        selectedCoordinate: CLLocationCoordinate2D?,
        places: [TuripMapPlace],
        placesPayload: String
    ) {
        if !isInitialized {
            moveCamera(cameraUpdateForPlaces(places, fallbackCoordinate: selectedCoordinate))
            isInitialized = true
            return
        }

        if previousPlacesPayload != placesPayload {
            animate(with: cameraUpdateForPlaces(places, fallbackCoordinate: selectedCoordinate))
            return
        }

        guard let selectedCoordinate else { return }
        guard previousSelectedCoordinate?.isSameCoordinate(as: selectedCoordinate) != true else { return }

        animateCamera(
            to: GMSCameraPosition.camera(
                withTarget: selectedCoordinate,
                zoom: defaultZoom
            ),
            duration: selectedPlaceAnimationDuration
        )
    }

    private func animateCamera(
        to cameraPosition: GMSCameraPosition,
        duration: CFTimeInterval
    ) {
        CATransaction.begin()
        CATransaction.setAnimationDuration(duration)
        animate(to: cameraPosition)
        CATransaction.commit()
    }

    private func cameraUpdateForPlaces(
        _ places: [TuripMapPlace],
        fallbackCoordinate: CLLocationCoordinate2D?
    ) -> GMSCameraUpdate {
        guard !places.isEmpty else {
            return GMSCameraUpdate.setTarget(
                fallbackCoordinate ?? CLLocationCoordinate2D(latitude: 0, longitude: 0),
                zoom: defaultZoom
            )
        }

        if places.count == 1 {
            return GMSCameraUpdate.setTarget(
                CLLocationCoordinate2D(latitude: places[0].latitude, longitude: places[0].longitude),
                zoom: defaultZoom
            )
        }

        var bounds = GMSCoordinateBounds()
        places.forEach { place in
            bounds = bounds.includingCoordinate(
                CLLocationCoordinate2D(latitude: place.latitude, longitude: place.longitude)
            )
        }
        return GMSCameraUpdate.fit(bounds, withPadding: boundsPadding)
    }

    private func parsePlaces(_ payload: String) -> [TuripMapPlace] {
        payload.split(separator: "\n").compactMap { row in
            let fields = row.split(separator: "\t", omittingEmptySubsequences: false)
            guard fields.count >= 4,
                  let latitude = Double(fields[1]),
                  let longitude = Double(fields[2]) else {
                return nil
            }

            return TuripMapPlace(
                latitude: latitude,
                longitude: longitude,
                name: String(fields[3])
            )
        }
    }
}

private struct TuripMapPlace {
    let latitude: Double
    let longitude: Double
    let name: String
}

private extension CLLocationCoordinate2D {
    var validOrNil: CLLocationCoordinate2D? {
        latitude == 0 && longitude == 0 ? nil : self
    }

    func isSameCoordinate(as other: CLLocationCoordinate2D) -> Bool {
        abs(latitude - other.latitude) < 0.000001 &&
            abs(longitude - other.longitude) < 0.000001
    }
}
