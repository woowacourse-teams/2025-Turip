import SwiftUI
import ComposeApp
import FirebaseCore
import FirebaseInstallations

@main
struct iOSApp: App {
    init() {
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
