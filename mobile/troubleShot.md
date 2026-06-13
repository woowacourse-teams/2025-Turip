# Troubleshooting Notes

Last updated: 2026-06-11

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
