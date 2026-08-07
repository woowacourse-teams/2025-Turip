# KMP Migration Progress

Last updated: 2026-06-11

## Migration Policy

- Original Android code is treated as the source of truth.
- When moving code to KMP, preserve original ViewModel, UseCase, DTO, API, and UI behavior as much as possible.
- Replace Android-only APIs with `expect` / `actual` platform implementations instead of simplifying business logic.
- Avoid adding placeholder or invented behavior when original Android implementation exists.

## Login

- Android Google login was connected through platform-specific implementation.
- Server login request body issue was fixed by configuring request serialization/content type correctly.
- Current Android login flow is working.
- iOS login is intentionally deferred because Android login is the current focus.

## Network

- `HttpClientFactory` / network setup was adjusted to stay closer to the original Android `NetworkModule`.
- API requests are now expected to follow the original Android behavior more closely.

## Home

- Home screen API integration was aligned with the current backend contract.
- Image loading issue was investigated and fixed.

## Turip

- Turip feature API integration was migrated.
- Folder add/delete text interpolation issues were checked and fixed where `%s` appeared literally.
- Current Turip flow is working after fixes.

## TuripDetail

- TuripDetail was migrated with the original Android code as reference.
- UI and business behavior were preserved as much as possible.
- Text/resource mismatch issue around "내 튜립" wording was checked and corrected.
- Google Maps migration was deferred because iOS Google Maps support was not a current blocker.

## TripDetail

- TripDetail screen was moved from sample/static UI toward ViewModel-backed API data.
- Bookmark update flow was connected through `UpdateBookmarkUseCase`.
- TripDetail platform-specific actions were split with `expect` / `actual`.
- Android platform actions include map open, web URL open, and share flow.
- iOS platform actions include URL opening and native share sheet behavior.

## TripDetail WebView

- Android WebView implementation was ported close to the original:
  - local `iframe.html`
  - YouTube iframe API
  - JavaScript bridge
  - `loadDataWithBaseURL`
  - fullscreen handling through `WebChromeClient`
- App `BASE_URL` is injected into TripDetail WebView setup through DI.
- Android WebView now uses app `BASE_URL` in `loadDataWithBaseURL`, matching the original Android behavior.
- iOS WebView initially used direct YouTube embed URL loading, then was changed to use HTML loading with `baseURL = BASE_URL`.
- iOS YouTube iframe HTML now passes `origin = BASE_URL` to avoid YouTube origin/referrer errors.
- iOS fullscreen orientation handling was added through `UIWindowSceneGeometryPreferencesIOS`.

## Verification

- `:feature:trip:impl:compileDebugKotlinAndroid` passed during TripDetail migration.
- `:feature:trip:impl:compileKotlinIosSimulatorArm64` passed after iOS WebView/base URL/orientation changes.
- `:composeApp:compileDebugKotlinAndroid` passed after Android fullscreen/splash orientation fix.
- `:composeApp:compileKotlinIosSimulatorArm64` passed before later iOS-only TripDetail WebView changes.

## Current Working Tree Notes

- Current modified file:
  - `feature/trip/impl/src/iosMain/kotlin/com/on/turip/feature/trip/impl/TripDetailWebViewController.ios.kt`
- Untracked file outside mobile workspace:
  - `../.DS_Store`
- New documentation:
  - `progress.md`
  - `troubleShot.md`
