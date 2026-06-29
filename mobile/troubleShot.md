# Troubleshooting Notes

Last updated: 2026-06-29

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

## iOS Login Failed Because DataStore Could Not Create Its File

### Symptom

- Google login succeeded and the server login API returned `accessToken` / `refreshToken`.
- The app then failed while saving tokens, so login was treated as failed.
- Logs included:
  - `okio.IOException: Operation not permitted`
  - `okio.PosixFileSystem#createDirectory`

### Cause

- This was not an auth failure or a server communication failure. It was an iOS local storage path error.
- The iOS DataStore file path was:
  - `NSHomeDirectory() + "/datastore/turip_prefs.preferences_pb"`
- That location is not a standard, reliably writable directory inside the iOS app sandbox.
- When DataStore tried to create the `/datastore` directory there, iOS denied directory creation.
- Android had no issue because it used `context.preferencesDataStoreFile("turip_prefs")`, which resolves to the correct internal storage location.

### Resolution

- In `core/local/src/iosMain/.../di/LocalModuleIos.kt`, changed the DataStore path base from `NSHomeDirectory()` to the standard `Library/Application Support` directory.
- Added a helper that resolves it and falls back to `NSHomeDirectory()` if unavailable:

```kotlin
single<DataStore<Preferences>> {
    PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            "${applicationSupportDirectory()}/datastore/turip_prefs.preferences_pb".toPath()
        },
    )
}

private fun applicationSupportDirectory(): String =
    NSSearchPathForDirectoriesInDomains(
        directory = NSApplicationSupportDirectory,
        domainMask = NSUserDomainMask,
        expandTilde = true,
    ).firstOrNull() as? String ?: NSHomeDirectory()
```

### Notes

- `Library/Application Support` may not exist on a clean install until the app creates it.
- DataStore (okio) creates the leaf `/datastore` folder, but if `Application Support` itself is missing, the same `Operation not permitted` error can recur.
- If this is reproduced on a clean device, create the directory first with `NSFileManager` before initializing DataStore.

## iOS 초대 딥링크 진입이 간헐적으로 엉뚱한 화면으로 감 (한글 기록)

### 증상

- iOS에서 초대 링크로 앱에 진입하면 목적지가 **간헐적으로 제각각**이었다.
  - 어쩔 때는 **홈 화면**
  - 어쩔 때는 **로그인 화면**
  - 어쩔 때는 정상적으로 **TuripDetail(초대받은 튜립 상세)**
- Android는 안정적으로 동작.

### 원인 ① — iOS 콜드 스타트 딥링크 이벤트 유실 (Race Condition)

- iOS는 딥링크를 `MutableSharedFlow(extraBufferCapacity = 1)` (replay = 0)으로만 전달했다.
- `extraBufferCapacity = 1`은 **구독자가 없을 때 값을 저장하는 공간이 아니다.** 구독자가 없으면 `replay` 값만 보관되는데 `replay = 0`이라 **콜드 스타트에 emit된 이벤트가 사라진다.**
- iOS `MainViewController`는 `initialDeepLinkUrl = null` 고정 → 콜드 스타트도 Flow에만 의존.
- SwiftUI(`iOSApp.swift`)의 `onOpenURL` / `onContinueUserActivity`가 앱 실행 즉시 `emitIosDeepLink()`를 호출하는데, Compose `MainApp`의 콜렉터는 컴포지션 이후에야 구독을 시작 → **emit이 구독보다 먼저면 유실**.
- 딥링크가 유실되면 Splash가 `deepLinkUrl = null`로 세션 기반 기본 진입을 한다.
  - 세션 `Member` → 홈, 세션 `Guest/Uninitialized` → 로그인, 딥링크 도착 시 → TuripDetail.
- Android가 안정적이었던 이유: `MainActivity`가 콜드 스타트 시 `intent.deepLinkUrl()`을 **동기적으로** 읽어 Splash에 주입(Flow 경쟁 없음).

### 해결 ①

- 딥링크를 **프로세스 수명 동안 보관**하는 구조로 전환(유실 차단).
  - `core/common`에 순수·테스트 가능한 딥링크 전달 계층 신설: `DeepLinkStore`(미소비 딥링크를 `MutableStateFlow`로 보관 + Collector 직렬화 + CAS `acknowledge`), `DeepLinkEventSource`, `DeepLinkEvent`, `ActionableDeepLinkRoute`, `DeepLinkParseResult`.
  - iOS `IosDeepLinkBridge`를 **프로세스 수명 싱글턴 object**로 변경(Store를 `MainViewController` 밖에 둠) → 컴포지션보다 먼저 offer해도 값이 보관되어 늦게 붙는 Collector에 전달.
  - Android는 기존 동작 유지를 위해 `PassthroughDeepLinkEventSource`로 warm-start Flow를 래핑.
  - 네비게이션 우선순위(DeepLink > Session)용 `StartupNavigationArbiter`, 중복 진입 방지용 `InvitationNavigationCoordinator` 추가.

### 2차 증상 — "더 이상해졌다"

- 1차 수정 후 **딥링크로 진입하면 로그인 화면에서 "서버에 오류가 발생했어요" 스낵바**가 떴다.
- 그런데 **클라이언트(Napier) 로그에는 아무것도 안 찍혔다.** ← 이게 경로를 좁히는 결정적 단서.
  - InvitationEntry 실패는 다이얼로그 + `Napier.e` → 해당 없음.
  - TuripDetail 로드 실패도 `handleLoadFailure`에서 `Napier.e` → 해당 없음.
  - 발생 화면이 로그인 → `LoginViewModel.initDeepLinkUrl`가 딥링크를 들고 진입 시 `RequestAutoLogin`을 자동 발생시키고, `handleError`는 로그 없이 서버 에러 스낵바만 띄움.

### 원인 ② — 세션 초기화 경쟁(Race)

- `SessionManager._state`는 `Uninitialized`로 시작, **Splash의 `switchSession`** 이 호출돼야 Member/Guest가 된다.
- 그런데 이제 딥링크가 즉시·확실하게 도착 → `MainApp` 콜렉터가 `goWithAllClear`로 InvitationEntry로 이동하며 **Splash를 백스택에서 제거** → Splash ViewModel scope 취소 → **세션 초기화 코루틴이 끝나기 전에 취소** → 세션이 `Uninitialized`로 남음.
- InvitationEntry가 `Uninitialized`를 보고 `DetermineInvitationEntryRouteUseCase`가 **RequiresAuth**로 판정(로그 안 남김) → 로그인으로 이동 → `RequestAutoLogin` 실패 → **서버 에러 스낵바(로그 없음)**.
- 즉 회원인데도 로그인으로 튕긴 것. 기존엔 딥링크가 자주 유실돼 이 경쟁이 안 드러났는데, 전달이 안정화되니 노출됐다. (Android는 딥링크가 Splash를 거쳐 세션 초기화 후 진입하므로 안전)

### 원인 ③ — 토큰 이중 디코딩 (1차 수정 때 새로 넣은 회귀)

- `MainApp`에서 딥링크 토큰으로 URL을 재조립했다: `"...?token=$token"`.
- `token`은 `InvitationTokenParser`가 **이미 한 번 디코딩**한 값인데, InvitationEntry가 이 URL을 받아 **또 디코딩** → 토큰에 `+`나 `%XX`가 있으면 **이중 디코딩으로 손상**(예: `+` → 공백).
- 기존 iOS 코드는 raw URL을 그대로 넘겨 한 번만 디코딩했는데, 토큰→URL 재조립을 넣으면서 깨졌다.

### 해결 ②③

- **세션 경쟁**: `InvitationEntryViewModel`이 토큰 검증 전에 세션 초기화를 직접 보장하도록 수정(Splash 경유 여부와 무관, 이미 초기화면 no-op).

```kotlin
private suspend fun ensureSessionInitialized() {
    if (sessionManager.state.value != SessionState.Uninitialized) return
    when (determineInitialSessionUseCase()) {
        AuthStatus.Authenticated -> sessionManager.switchToMember()
        AuthStatus.UnAuthenticated -> sessionManager.switchToGuest()
    }
}
```

- **이중 디코딩**: `DeepLinkEvent`에 **원본 URL을 보존**(`originalUrl`, `toString`은 redacted)하고, 재조립 대신 원본을 그대로 `InvitationEntryNavKey`에 전달.

```kotlin
navigator.goWithAllClear(InvitationEntryNavKey(event.originalUrl))
```

### 교훈

- `MutableSharedFlow(replay = 0)`는 구독자가 없을 때 emit된 값을 잃는다. 생명주기/구독 타이밍 경쟁이 있는 이벤트 전달에는 보관형(StateFlow) + 명시적 acknowledge가 안전.
- 버그를 고치면 가려져 있던 다른 버그가 드러날 수 있다(딥링크 안정화 → 세션 경쟁 표면화).
- "증상 + 로그 유무"를 함께 보면 경로를 빠르게 좁힐 수 있다("서버 에러 스낵바인데 로그가 없다" → 로그를 안 남기는 특정 경로 지목).
- 파싱해서 꺼낸 토큰을 다시 URL로 조립하면 이중 디코딩으로 손상될 수 있으니 **원본을 그대로 보존**해 전달한다.
- iOS는 딥링크가 Splash를 건너뛸 수 있다. 세션 초기화가 특정 화면 생명주기에 묶이면 그 화면을 건너뛰는 경로에서 누락될 수 있으니, **진입 화면이 스스로 초기화를 보장**하게 만든다.

### 검증

- Android 빌드(`assembleDebug`), iOS 네이티브 컴파일(`compileKotlinIosSimulatorArm64`) 통과.
- 공통 자동 테스트(`:core:common:iosSimulatorArm64Test`) 19개 통과(콜드 스타트 전 offer→전달, CAS, Collector 직렬화, Unsupported 폐기, latest-wins, Arbiter owner 선점/예외 복원, 토큰 비노출, 원본 URL 보존 등).
- 실기기에서의 딥링크 콜드 스타트 반복 테스트는 별도 수행 필요.

## 초대 링크 페이지 "앱으로 열기"가 컨텍스트마다 다르게 동작 (한글 기록)

### 증상

- 초대 링크 페이지(`invite.turip.kro.kr/invitations/`)에서 "앱으로 열기"가 환경마다 제각각이었다.
  - **카카오톡 인앱**: 상단 "앱 열기" 바는 안 뜨지만, **"앱으로 열기" 버튼은 됨**.
  - **iOS Safari**: **"앱으로 열기" 버튼이 안 됨.** 대신 상단 "열기" 바를 눌러야 앱이 열림.
- 두 환경에서 각각 다른 경로 하나만 살아 있어 사용자가 혼란.

### 원인

- 두 "앱 열기"는 **작동 메커니즘이 다른 별개**였다.
  - **"앱으로 열기" 버튼** = `turip://` **커스텀 스킴**. 카카오 WKWebView에서는 동작하지만 **iOS Safari에서는 실패**.
  - **상단 바** = iOS 시스템의 앱 열기 UI(Universal Link 계열). invite 호스트 AASA + 설치된 앱의 entitlement(`applinks:invite.turip.kro.kr`)가 모두 갖춰져 동작.
- ⚠️ 핵심 제약 — **같은 호스트 Universal Link로는 Safari가 앱을 안 연다.** 현재 페이지와 목적지가 둘 다 `invite.turip.kro.kr`이면, Safari에서 같은 호스트 Universal Link를 눌러도 앱이 아니라 Safari에 그대로 남는다(Apple 공식 동작). → **앱 실행 전용 별도 호스트가 필요**.
- 용어 정정: **Smart App Banner ≠ Universal Link** (별개 메커니즘). 현재 페이지엔 `apple-itunes-app` 메타가 없음.

### 해결 방향 — 앱 실행 전용 호스트 `open.turip.kro.kr` 신설

- 역할 분리: 랜딩 = `invite.turip.kro.kr`(현행), **앱 실행 링크 = `open.turip.kro.kr`(신규)**.
- 신규 호스트를 **별도 GitHub Pages repo**(`jerry8282/turip-open`)로 구축.
  - **GitHub Pages는 repo 1개당 커스텀 도메인 1개**(CNAME 한 줄)라 invite repo에 못 합치고 새 repo가 강제됨.
  - `.well-known/apple-app-site-association`(iOS) + `.well-known/assetlinks.json`(Android) + 웹 폴백 `invitations/index.html` 배포.
  - DNS `open.turip.kro.kr` → `jerry8282.github.io` CNAME, Enforce HTTPS(Let's Encrypt 자동 발급, 약 48분 소요).
- iOS: `iosApp.entitlements`의 Associated Domains에 `applinks:open.turip.kro.kr`(+`?mode=developer`) 추가(invite는 유지).
- 웹: `index.html`을 호스트 비의존으로 리팩터 — `APP_OPEN_HOST` 상수 하나만 채우면 iOS 일반 브라우저 CTA가 `turip://`에서 `https://open.turip.kro.kr` Universal Link로 전환되도록 준비.

### 결정 — Android는 `intent://` 유지 (open은 iOS 전용)

- open 호스트가 **필요했던 건 iOS 때문**(Safari same-host 제약). Android는 `intent://`가 이미 어느 브라우저에서든 앱 자동 실행 + 미설치 시 Play Store 폴백까지 정상 동작.
- "되는 걸 바꾸는" 비용·검증만 늘 뿐 실익이 적어, **Android App Link 통일은 폐기**하고 앱·매니페스트 변경을 원복. open 호스트는 iOS 전용으로 운영.
- `index.html`도 `generalAppHref = isAndroid ? intentUrl : (appLinkUrl || …)`로 고정 → 나중에 `APP_OPEN_HOST`를 채워도 **Android는 항상 `intent://`** (미등록 open 링크로 보내 앱 대신 웹으로 떨어지는 것 방지).

### 가장 큰 함정 — "curl 200 = 앱 연결됨"이 아니다

- **Universal Link는 양쪽 악수(handshake)가 모두 있어야 앱이 열린다.**
  - 웹쪽: `open.turip.kro.kr/.well-known/AASA`가 앱 ID 선언 → 배포 완료.
  - 앱쪽: **사용자 기기에 설치된 앱**의 entitlement가 `applinks:open.turip.kro.kr` 선언 → **출시 빌드에 아직 없음**.
- iOS는 **앱 설치 시점에** entitlement에 적힌 도메인의 AASA만 가져간다. 설치된 앱에 open이 없으면, open 링크를 눌러도 iOS는 담당 앱이 없다고 보고 **Safari로 웹페이지만** 연다.
- 그래서 `open.turip.kro.kr`이 HTTPS 200으로 살아 있어도(웹 준비 완료), **앱 출시 전까지는 Safari에서 앱이 열리지 않는다.**
- 현재 Safari 상단 바가 되는 이유: 출시된 앱이 이미 `invite`를 entitlement에 갖고 있어 invite는 양쪽 악수가 완성돼 있기 때문.

### 배포 순서 (웹 CTA 플립은 맨 마지막)

1. `open.turip.kro.kr` DNS·HTTPS 구성 ✅
2. AASA + assetlinks + 웹 폴백 배포 ✅
3. iOS 앱에 entitlement 포함 빌드
4. 앱 업데이트 출시
5. 설치된 앱으로 Universal Link 실기기 검증
6. (마지막) `index.html` `APP_OPEN_HOST` 플립 → 이때 비로소 Safari "앱으로 열기" 정상화
- 웹을 먼저 플립하면 구버전 앱 사용자가 미등록 호스트로 가서 웹 폴백으로 떨어지므로 **순서 중요**.

### GitHub Pages 한계 (인지 사항)

- AASA의 `Content-Type`이 `application/octet-stream`으로 나감(확장자 없는 파일). 단 invite에서 이미 정상 동작 중이고 최신 iOS는 강제하지 않아 무방. assetlinks는 `.json`이라 `application/json` 정상.
- 토큰 페이지의 `Cache-Control: no-store`, `Referrer-Policy: no-referrer` **응답 헤더는 GitHub Pages로 설정 불가**(커스텀 헤더 미지원). 현재는 `<meta robots/referrer>`로 메타 수준만 대체. 헤더 레벨이 필수면 Cloudflare/Netlify/Vercel로 이전 검토.

### 교훈

- Smart App Banner(상단 바)와 Universal Link, 커스텀 스킴(`turip://`)은 모두 다른 메커니즘이다. "앱 열기"가 안 될 때 어느 경로인지부터 구분해야 한다.
- Safari는 **현재 페이지와 같은 호스트**의 Universal Link로는 앱을 안 연다 → 앱 실행 전용 호스트를 분리한다.
- Universal Link는 웹(AASA) + 앱(entitlement) **양쪽이 모두 출시·배포**돼야 작동한다. `curl 200`은 웹 절반일 뿐이다.
- "되는 걸(Android intent://) 굳이 바꾸지 않는다" — 통일이라는 명분보다 회귀 위험·검증 비용을 따져 범위를 좁힌다.

### 검증

- `open.turip.kro.kr` HTTPS 200·무리다이렉트 확인, `https_enforced: true`, HTTP→HTTPS 301.
- AASA appIDs(`B68RUUV6LW`/`C2ZQUWKQD6.com.on.turip`)·assetlinks packages(`com.on.turip`(+`.debug`)) JSON 유효성 확인.
- Android 매니페스트 변경 원복 확인(iOS entitlements만 유지). `index.html`은 노드 목으로 7개 컨텍스트 분기·토큰 인코딩 보존 확인.
- 실기기 Universal Link(앱 출시 후) 및 iOS "앱 설치하기" App Store ID 확보는 별도 수행 필요.
