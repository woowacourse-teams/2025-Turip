import SwiftUI
import ComposeApp
import FirebaseCore
import FirebaseInstallations
import GoogleMaps

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
            updater: { view, selectedLatitude, selectedLongitude, selectedName, placesPayload in
                guard let mapView = view as? TuripGoogleMapView else { return }
                mapView.update(
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

    func configure() {
        settings.zoomGestures = true
        settings.scrollGestures = true
        settings.rotateGestures = false
        settings.tiltGestures = false
    }

    func update(
        selectedLatitude: Double,
        selectedLongitude: Double,
        selectedName: String,
        placesPayload: String
    ) {
        let selectedCoordinate = CLLocationCoordinate2D(
            latitude: selectedLatitude,
            longitude: selectedLongitude
        )

        clear()

        var selectedMarker: GMSMarker?
        parsePlaces(placesPayload).forEach { place in
            let marker = GMSMarker()
            marker.position = CLLocationCoordinate2D(latitude: place.latitude, longitude: place.longitude)
            marker.title = place.name
            marker.map = self

            if abs(place.latitude - selectedLatitude) < 0.000001 &&
                abs(place.longitude - selectedLongitude) < 0.000001 {
                selectedMarker = marker
            }
        }

        animate(to: GMSCameraPosition.camera(
            withTarget: selectedCoordinate,
            zoom: defaultZoom
        ))

        if let selectedMarker {
            self.selectedMarker = selectedMarker
        } else if !selectedName.isEmpty {
            let marker = GMSMarker(position: selectedCoordinate)
            marker.title = selectedName
            marker.map = self
            self.selectedMarker = marker
        }
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
