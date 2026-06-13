# Troubleshooting Notes

Last updated: 2026-06-14

## Android Google Login Did Not Navigate

### Symptom

- Google login UI returned to the app, but the next screen did not open.
- Logs included:
  - `GetCredentialResponse error returned from framework`
  - `GetCredentialException.TYPE_USER_CANCELED`
  - `[16] Account reauth failed`

### Cause

- Credential Manager returned cancellation/reauth failure before a valid Google credential was available.
- This is different from a successful Google token followed by server login failure.

### Resolution

- Added clearer logging around Android Google Credential Manager failure.
- Separated Google credential failure from server login failure while debugging.

## Server Login Request Body Failed

### Symptom

- Google login succeeded, but server login failed.
- Log:
  - `Fail to prepare request body for sending`
  - `Content-Type: null`
  - body type: `LoginIdTokenPostRequest`

### Cause

- Ktor request body serialization/content type was not configured correctly for the migrated KMP request.

### Resolution

- Fixed request setup so the login DTO can be serialized and sent with the proper content type.
- Android login flow worked after this fix.

## Literal `%s` Appeared In UI Text

### Symptom

- Folder add/delete dialogs showed `%s` literally.
- Examples:
  - `%s 를 추가했다고`
  - `%s를 삭제하시겠습니까`

### Cause

- String formatting from Android resources was not being applied correctly after migration.

### Resolution

- Searched for remaining `%s` usage in migrated UI/resource code.
- Fixed affected text paths so formatted values are rendered instead of the raw placeholder.

## Image Loading Did Not Work

### Symptom

- Images were not loading across screens after migration.

### Cause

- The migrated KMP image loading path did not match how remote image URLs and loading components needed to be used in common UI.

### Resolution

- Adjusted image loading usage so remote images render correctly in migrated screens.

## Android Emulator Local HTTP Server Blocked

### Symptom

- iOS Simulator could connect to the local server with:
  - `http://localhost:8080/api/`
- Android Emulator used the correct host-machine alias:
  - `http://10.0.2.2:8080/api/`
- Android requests still failed with:

```text
java.net.UnknownServiceException: CLEARTEXT communication to 10.0.2.2 not permitted by network security policy
```

### Cause

- `10.0.2.2` is the correct Android Emulator address for the host machine.
- The failure was not an emulator routing issue.
- Android blocked plain `http://` traffic because cleartext traffic was not allowed by the app manifest/network security policy.

### Resolution

- Added a manifest placeholder for cleartext traffic:
  - debug: `usesCleartextTraffic = true`
  - release: `usesCleartextTraffic = false`
- Wired the placeholder into `AndroidManifest.xml`:

```xml
android:usesCleartextTraffic="${usesCleartextTraffic}"
```

- `:composeApp:compileDebugKotlinAndroid` passed after the fix.

### Notes

- Use `http://10.0.2.2:8080/api/` for Android Emulator.
- Use `http://localhost:8080/api/` for iOS Simulator.
- Use the host machine LAN IP, not `10.0.2.2`, for Android physical devices.
- Reinstall/re-run the Android app after manifest changes so the new network policy is applied.

## YouTube Error 152-4 On Android WebView

### Symptom

- YouTube video failed in TripDetail WebView with error `152-4`.

### Cause

- The migrated WebView path was not using the app domain as the base URL the same way original Android did.
- YouTube iframe API is sensitive to origin/referrer.

### Resolution

- Injected app `BASE_URL` into TripDetail WebView setup.
- Android actual now passes `BASE_URL` to `loadDataWithBaseURL`.
- This restored original Android behavior and fixed video playback.

## Android Fullscreen Video Showed Splash / Felt Like App Restarted

### Symptom

- Tapping fullscreen on Android video switched to a splash-like state and did not return normally.
- It felt like the app restarted.

### Cause

- Fullscreen video requested landscape orientation.
- KMP Android manifest did not include the original Android activity `configChanges`.
- Activity recreation happened on orientation change, and app navigation started again from the splash entry.
- Orientation lock logic inside Compose also conflicted with fullscreen behavior.

### Resolution

- Added original-style activity configuration:
  - `android:launchMode="singleTop"`
  - `android:configChanges="keyboardHidden|orientation|screenSize"`
  - `android:windowSoftInputMode="adjustResize"`
- Moved Android orientation lock out of Compose recomposition and into `MainActivity.onCreate`.
- `:composeApp:compileDebugKotlinAndroid` passed after the fix.

## YouTube Error 153 On iOS WebView

### Symptom

- Android video worked, but iOS WebView showed YouTube error `153`.
- Similar previous issue happened when the app domain was not passed correctly to YouTube API calls.

### Cause

- iOS actual loaded `https://www.youtube.com/embed/...` directly with `WKWebView.loadRequest`.
- Unlike Android, iOS was not loading iframe HTML with app `BASE_URL` as the base URL.
- YouTube could not see the expected origin/referrer.

### Resolution

- Changed iOS actual to load generated iframe API HTML through `WKWebView.loadHTMLString`.
- Passed `baseURL = BASE_URL`.
- Added `playerVars.origin = BASE_URL`.
- Kept `referrerpolicy = strict-origin-when-cross-origin`.
- `:feature:trip:impl:compileKotlinIosSimulatorArm64` passed after the fix.

## iOS WebKit / Simulator Noise Logs

### Symptom

- Logs included:
  - `Unable to filter tracking query parameters`
  - `Unable to hide query parameters from script`
  - `RBSServiceErrorDomain ... WebKit Media Playback`
  - `RTIInputSystemClient ... valid sessionID`
  - `VisionKit.RemoveBackground unsupported device`
  - `commcenter.coretelephony.xpc was invalidated`

### Cause

- These are mostly iOS Simulator/WebKit/system service logs.
- They are not necessarily app errors if video playback and navigation work.

### Resolution

- No app-side fix required unless a real user-visible failure appears.
- Focus on actual YouTube player errors, WK navigation failures, app crashes, or broken UI behavior.

## iOS Fullscreen Video Did Not Rotate To Landscape

### Symptom

- Android fullscreen video automatically rotated to landscape.
- iOS fullscreen video stayed portrait.

### Cause

- iOS `HandleFullScreenWindowLaunchedEffect` actual was empty.
- `Info.plist` already allowed portrait and landscape, so the missing part was the runtime orientation request.

### Resolution

- Implemented iOS fullscreen orientation request with `UIWindowSceneGeometryPreferencesIOS`.
- Fullscreen requests `UIInterfaceOrientationMaskLandscape`.
- Dispose/exit requests `UIInterfaceOrientationMaskPortrait`.
- `:feature:trip:impl:compileKotlinIosSimulatorArm64` passed after the fix.

## iOS Share Sheet Did Not Appear

### Symptom

- TuripDetail/TripDetail invite link API succeeded, but the iOS share sheet did not appear.
- Logs confirmed the API response was successful.
- After switching to `kmp-sharing`, app logs showed:
  - `iOS kmp-sharing requested. textLength=...`
- That meant the UI effect and platform action reached the share call, but presentation still failed or was hidden.
- Simulator/system noise logs also appeared, such as:
  - `RTIInputSystemClient ... valid sessionID`
  - `CHHapticPattern ... hapticpatternlibrary.plist couldn't be opened`

### Cause

- The original direct iOS implementation used `UIActivityViewController` and `UIApplication.sharedApplication.keyWindow?.rootViewController`.
- `kmp-sharing` internally also presents from `UIApplication.sharedApplication.keyWindow?.rootViewController`.
- In Compose Multiplatform iOS, presenting a native share sheet immediately while a Compose modal bottom sheet is still being dismissed can silently fail or appear to do nothing.
- The system haptic/keyboard logs were simulator noise, not the root cause.

### Resolution

- Replaced direct share implementations with `kmp-sharing`.
- Added `com.swmansion.kmpsharing:kmp-sharing:0.2.0`.
- Used `rememberShare()` for TuripDetail/TripDetail share actions.
- On iOS, wrapped invitation links as text:
  - `튜립 초대 링크\n$link`
- Passed `iosUTI = "public.plain-text"` for text sharing.
- Removed custom iOS anchor and let `kmp-sharing` use its default centered anchor.
- In `TuripDetailScreen`, hid the more-option bottom sheet first and delayed share by `250ms`.
- `:feature:turipdetail:impl:compileKotlinIosSimulatorArm64 :feature:trip:impl:compileKotlinIosSimulatorArm64` passed after the fix.

### Notes

- The share call should log:

```text
iOS kmp-sharing requested. textLength=...
```

- If this log appears but the sheet does not, re-check native presentation timing or root presenter selection.
- If this log does not appear, debug the UI effect/platform action path first.

## Gradle Project Sync Broken Pipe

### Symptom

- Android Studio Gradle project sync failed with:
  - `Broken pipe`
  - `The Gradle daemon may be trying to use ipv4 instead of ipv6`

### Cause

- CLI Gradle commands still worked, so this is likely Android Studio to Gradle daemon local socket communication rather than project source code.
- Project `gradle.properties` does not currently set IPv4/IPv6 preference.
- Daemon logs show local daemon addresses using `localhost/127.0.0.1`.

### Recommended Resolution

1. Stop daemons and restart Android Studio:

```bash
./gradlew --stop
```

2. If sync still fails, test IPv6 preference locally in `~/.gradle/gradle.properties` first:

```properties
org.gradle.jvmargs=-Xmx2048M -Dfile.encoding=UTF-8 -Djava.net.preferIPv6Addresses=true
kotlin.daemon.jvmargs=-Xmx2048M -Djava.net.preferIPv6Addresses=true
```

3. Only move this setting into project `gradle.properties` if it is confirmed to be needed for the team.

## iOS TuripDetail Map Uses Google Maps SDK

### Context

- Android TuripDetail already used Google Maps through `com.google.maps.android:maps-compose`.
- iOS TuripDetail originally used MapKit through `MKMapView`.
- Product decision: use Google Maps on iOS as well, not WebView and not MapKit.

### Checked Alternative

- Checked `eu.buney.maps:kmp-maps-compose` from `yankeppey/kmp-maps-compose`.
- The library artifact is built with Kotlin `2.3.x` KLIB ABI.
- Current project Kotlin is `2.2.21`, so iOS compilation cannot consume that KLIB.
- Using this library would require upgrading the project Kotlin/KSP stack, not just changing the map module.

### Resolution

- Added Google Maps iOS SDK through Swift Package Manager in the Xcode project.
- Added a small KMP bridge so common/iOS Kotlin can provide the map data and Swift can render the native `GMSMapView`.
- `iosApp/iosApp/iOSApp.swift` now:
  - calls `GMSServices.provideAPIKey(...)`
  - registers a native `TuripGoogleMapView` factory/updater
  - renders markers and selected place with Google Maps SDK
- TuripDetail iOS map now uses native Google Maps SDK instead of MapKit.

### API Key Notes

- Google Maps API key is read from `local.properties` as `google_maps_api_key`.
- `local.properties` is ignored by Git, so the raw key is not committed to the repository.
- The key is passed into BuildKonfig and used by iOS at runtime.
- This means the key is not exposed in GitHub, but it is still included in the mobile app binary.
- Protect the key in Google Cloud Console with platform restrictions:
  - iOS: bundle identifier restriction
  - Android: package name and SHA-1 restriction

### Notes

- `iosApp/Configuration/Config.xcconfig` is tracked, but it currently contains Google Sign-In configuration such as `GOOGLE_REVERSED_CLIENT_ID`, not the Google Maps API key.
- A future switch to `kmp-maps-compose` should be handled as a separate Kotlin `2.3.x` upgrade task.
