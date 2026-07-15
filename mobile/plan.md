# FCM 토큰 등록 / 알림 설정 API 연동 계획

## 대상 API

| API | 메서드 | 설명 | 성공 응답 |
|-----|--------|------|-----------|
| `/api/v1/fcm-tokens` | POST | FCM 토큰 등록/갱신 (Body: `token`) | 200, Body 없음 |
| `/api/v1/fcm-tokens/notification` | PATCH | 알림 수신 여부 변경 (Body: `notificationEnabled`) | 204, Body 없음 |

두 API 모두 `device-fid` + `Authorization` 헤더 필수.
- `device-fid`: `HttpClientFactory.defaultRequestInterceptor`가 자동으로 붙임 (`core/network/di/HttpClientFactory.kt:128`) → Service에 헤더 파라미터 **불필요**
- `Authorization`: Ktor Auth(Bearer) 플러그인이 자동 처리 → `DEFAULT_KTORFIT` 클라이언트 사용하면 됨
- 단, **로그인(Member) 상태에서만 호출 가능** — 게스트 세션에서는 호출하지 않아야 함

## 현재 상태 진단

1. **Firebase Messaging 미도입**: 프로젝트에는 `firebase-installations`(FID용)만 있음 (`gradle/libs.versions.toml:64`). FCM 디바이스 토큰을 얻을 수단이 없어 **의존성 추가부터 필요**. `google-services` 플러그인과 `google-services.json`은 이미 composeApp에 설정돼 있음.
2. **`NotificationSettingScreen`은 로컬 상태만 존재**: `remember { mutableStateOf(true) }`로 스위치만 동작하고 ViewModel/서버 연동 없음 (`feature/mypage/impl/.../notificationsetting/NotificationSettingScreen.kt:42`).
3. **알림 권한 요청은 이미 존재**: `NotificationPermissionEffect`가 `MainApp.kt:138`에서 실행됨(Android 13+). 권한 결과 콜백(`onResult = {}`)은 현재 무시됨.
4. **알림 설정 조회(GET) API 없음**: 서버 명세에 현재 설정값을 읽는 API가 없으므로, 클라이언트가 마지막 설정값을 로컬(DataStore)에 저장해 화면 초기값으로 사용해야 함.

## 구현 단계

### 1단계 — 에러 태그/타입 추가

새 태그 3개: `FCM_TOKEN_BLANK`(400), `NOTIFICATION_ENABLED_REQUIRED`(400), `FCM_TOKEN_NOT_FOUND`(404)

- `core/model/.../result/ErrorType.kt` — `ErrorType.FcmToken` sealed interface 추가 (`Blank`, `NotificationEnabledRequired`, `NotFound`)
- `core/common/.../NetworkError.kt` — 동일 구조로 `NetworkError.FcmToken` 추가
- `core/network/.../error/ErrorMapping.kt` — `ErrorTag`에 3개 상수 + `toNetworkError()` 분기 추가
- `core/common/.../ErrorExtension.kt` — tag → `ErrorType` 매핑 분기 추가

### 2단계 — DTO (core:data)

```
core/data/.../dto/fcm/
├── FcmTokenRegisterRequest.kt        # @Serializable, val token: String
└── FcmNotificationEnabledRequest.kt  # @Serializable, val notificationEnabled: Boolean
```

### 3단계 — Service (core:network)

`core/network/.../service/FcmTokenService.kt` (Ktorfit):

```kotlin
interface FcmTokenService {
    @POST(ApiPath.V1 + "fcm-tokens")
    suspend fun postFcmToken(@Body request: FcmTokenRegisterRequest)

    @PATCH(ApiPath.V1 + "fcm-tokens/notification")
    suspend fun patchNotificationEnabled(@Body request: FcmNotificationEnabledRequest)
}
```

`ServiceModule.kt`에 `single<FcmTokenService> { get<Ktorfit>(named(DEFAULT_KTORFIT)).createFcmTokenService() }` 등록.

### 4단계 — DataSource

- `core/data/.../datasource/FcmTokenRemoteDataSource.kt` (인터페이스):
  - `suspend fun postFcmToken(request: FcmTokenRegisterRequest): TuripResult<Unit>`
  - `suspend fun patchNotificationEnabled(request: FcmNotificationEnabledRequest): TuripResult<Unit>`
- `core/network/.../datasourceimpl/DefaultFcmTokenRemoteDataSource.kt`:
  - `DefaultBookmarkRemoteDataSource` 패턴 그대로 — `withContext(Dispatchers.IO) { safeApiCall { ... } }`
- `DatasourceModule.kt`에 등록.

### 5단계 — Repository + 로컬 저장 (기기별 설정값 보존)

알림 설정값은 기기(로컬)에도 저장한다. 이 프로젝트는 SharedPreferences 대신 KMP 공용 `DataStore<Preferences>`를 이미 사용 중(`core/local/.../DefaultUserStorageLocalDataSource.kt` — device_fid, 토큰 저장)이므로 같은 방식을 따른다.

- **로컬 DataSource**:
  - `core/data/.../datasource/NotificationSettingLocalDataSource.kt` (인터페이스):
    - `suspend fun saveNotificationEnabled(enabled: Boolean): Result<Unit>`
    - `suspend fun getNotificationEnabled(): Result<Boolean?>` (저장값 없으면 null)
  - `core/local/.../datasourceimpl/DefaultNotificationSettingLocalDataSource.kt`:
    - `DefaultUserStorageLocalDataSource` 패턴 그대로 — 주입받은 `DataStore<Preferences>`에 `booleanPreferencesKey("notification_enabled")`로 저장
  - `LocalModule.kt`에 등록
- **Repository** — 원격 + 로컬을 함께 책임:
  - `core/domain/.../repository/FcmTokenRepository.kt`:
    - `suspend fun registerToken(token: String): TuripResult<Unit>`
    - `suspend fun updateNotificationEnabled(enabled: Boolean): TuripResult<Unit>` — PATCH 성공 시 내부에서 로컬에도 저장
    - `suspend fun getNotificationEnabled(): Boolean` — 로컬 저장값 반환, 없으면 기본 true
  - `core/data/.../repository/DefaultFcmTokenRepository.kt` — `FcmTokenRemoteDataSource` + `NotificationSettingLocalDataSource` 주입 (응답 Body 없으므로 매퍼 불필요)
  - `DataModule.kt`에 등록.

### 6단계 — FCM 토큰 획득 수단 (선행 인프라)

FID(`FidFetcher`)와 동일한 expect/actual 패턴을 `core:local`에 추가:

- `core/local/build.gradle.kts` — androidMain에 `firebase-messaging` 의존성 추가 (`libs.versions.toml`에 라이브러리 정의)
- `core/local/.../fcm/FcmTokenFetcher.kt` (commonMain): `internal expect suspend fun fetchPlatformFcmToken(): String?`
- androidMain actual: `FirebaseMessaging.getInstance().token.await()` + `runCatching`
- iosMain actual: `provideIosFirebaseInstallationId`와 동일한 `CompletableDeferred` 브릿지 패턴 (`provideIosFcmToken(token: String?)`) — Swift 쪽에서 FCM 토큰 전달, timeout 시 null 반환
- 도메인 접근용 인터페이스: `core/domain/.../fcm/FcmTokenProvider.kt` (인터페이스) + `core:local`에 구현 — `DeviceFidManager` 배선 방식과 동일

#### iOS 앱(Swift) 측 작업

현재 iosApp에는 `FirebaseCore` + `FirebaseInstallations`만 SPM으로 링크돼 있고, Push capability와 AppDelegate가 없음. 다음을 추가:

1. **Xcode 프로젝트**: SPM `firebase-ios-sdk`에서 `FirebaseMessaging` 제품 추가, Signing & Capabilities에서 Push Notifications 추가 (`iosApp.entitlements`에 `aps-environment` 생성됨). 필요 시 Background Modes → Remote notifications 체크.
2. **`iOSApp.swift`**: SwiftUI 라이프사이클이므로 `@UIApplicationDelegateAdaptor`로 AppDelegate 추가
   - `didFinishLaunching`에서 `FirebaseApp.configure()`(기존 `configureFirebaseInstallationId`와 중복 호출 안 되게 정리) + `UIApplication.shared.registerForRemoteNotifications()`
   - `didRegisterForRemoteNotificationsWithDeviceToken`에서 `Messaging.messaging().apnsToken = deviceToken`
   - `MessagingDelegate.messaging(_:didReceiveRegistrationToken:)`에서 Kotlin 브릿지 `provideIosFcmToken(token:)` 호출 (토큰 갱신 시에도 이 델리게이트가 다시 불리므로 갱신 대응 포함)
3. **composeApp iosMain**: `IosFirebaseInstallationBridge.kt`와 나란히 `IosFcmTokenBridge.kt` 추가 (Swift에서 호출할 top-level 함수)

**외부 선행 조건(코드 아님)**: Apple Developer 콘솔에서 APNs 인증 키(.p8) 발급 → Firebase 콘솔 프로젝트 설정에 업로드. 이게 없으면 iOS에서 FCM 토큰 발급이 실패하지만, 코드는 null-safe라 앱 동작에는 지장 없음 — 키 등록만 되면 코드 수정 없이 동작.

### 7단계 — 토큰 등록 트리거

`core/domain/.../usecase/RegisterFcmTokenUseCase.kt`:

```kotlin
class RegisterFcmTokenUseCase(
    private val fcmTokenProvider: FcmTokenProvider,
    private val fcmTokenRepository: FcmTokenRepository,
) {
    suspend operator fun invoke(): TuripResult<Unit> {
        val token = fcmTokenProvider.fetchToken() ?: return /* 실패 처리 */
        return fcmTokenRepository.registerToken(token)
    }
}
```

호출 시점 (모두 Member 세션일 때만):
1. **앱 시작 시** — `MainApp`(또는 세션 초기화 완료 지점)에서 세션이 `SessionState.Member`로 확정되면 1회 호출
2. **로그인 성공 시** — `SessionManager.switchToMember()` 이후
3. **알림 권한 승인 직후** — `NotificationPermissionEffect`의 `onResult` 콜백 활용 (현재 무시되고 있음)
4. **(Android) `onNewToken` 갱신 시** — `composeApp/src/androidMain`에 `FirebaseMessagingService` 구현체 추가 + `AndroidManifest.xml` 등록. 푸시 수신 처리 자체는 별도 작업으로 분리 가능하나 `onNewToken` 재등록은 이번에 포함 권장.

실패는 사용자에게 노출하지 않고 로그(Napier)만 남김 — 다음 시작 시 재시도되는 구조라 치명적이지 않음.

### 8단계 — NotificationSettingScreen MVIA 전환

현재 로컬 상태 스위치를 표준 MVIA 구조로 재작성:

```
feature/mypage/impl/.../notificationsetting/
├── NotificationSettingScreen.kt              # koinViewModel() + effect collect
├── NotificationSettingViewModel.kt           # BaseViewModel 상속
├── NotificationSettingState.kt               # isPushNotificationEnabled, isSystemNotificationEnabled, isLoading, dialogState
├── NotificationSettingIntent.kt              # ToggleNotification(enabled), UpdateSystemPermission(granted), ClickGoToSettings, DismissDialog, ClickBack
├── NotificationSettingEffect.kt              # ShowErrorMessage, OpenNotificationSettings 등
└── platform/
    └── NotificationPermissionActions.kt      # expect/actual (아래 참조)
```

동작 흐름:
- **초기값**: ViewModel init에서 `fcmTokenRepository.getNotificationEnabled()`로 로컬 저장값 로드 (없으면 기본 true) — 5단계의 DataStore 기반 로컬 저장 사용.
- **토글 시**: 낙관적 업데이트(스위치 즉시 반영) → `updateNotificationEnabled(enabled)` 호출
  - 성공: Repository 내부에서 로컬 DataStore에 값 저장 (5단계 참조)
  - 실패 `ErrorType.FcmToken.NotFound`(404): `RegisterFcmTokenUseCase`로 토큰 재등록 → PATCH 1회 재시도
  - 그 외 실패: 스위치 롤백 + 스낵바(Effect) — 로컬 저장값도 갱신하지 않음
- **연타 방지**: 진행 중 요청 있으면 무시하거나 마지막 값만 반영 (기존 Filter Debounce 패턴 참고 가능)
- `MyPageModule.kt`에 `viewModel<NotificationSettingViewModel>` 등록, `MyPageNavKeyProvider.kt:36`의 entry에서 주입.

#### OS 권한 거부 상태의 스위치 UX (범위 포함)

OS 알림 권한과 서버 설정은 별개이므로, 권한 거부 상태에서 스위치만 켜지면 "켰는데 알림이 안 오는" 상태가 됨. 처리 방식:

- **권한 상태 조회**: `rememberMyPagePlatformActions`와 동일한 expect/actual 패턴으로 `NotificationPermissionActions` 추가
  - `isNotificationsEnabled(): Boolean` — Android: `NotificationManagerCompat.from(context).areNotificationsEnabled()` / iOS: `UNUserNotificationCenter.getNotificationSettings` (비동기이므로 suspend로 래핑)
  - `openNotificationSettings()` — Android: `Settings.ACTION_APP_NOTIFICATION_SETTINGS` 인텐트 / iOS: `UIApplication.openSettingsURLString`
- **갱신 시점**: Screen에서 `LifecycleResumeEffect`(ON_RESUME)마다 권한 상태를 조회해 `UpdateSystemPermission` Intent로 전달 — 설정 앱에 갔다가 돌아왔을 때 자동 반영
- **스위치 ON 시도 + 권한 거부 상태**: "기기 알림이 꺼져 있어요" 다이얼로그 표시(`MyPageDialogState` 패턴 참고) → [설정으로 이동] 시 `openNotificationSettings()`. 서버 PATCH(ON)는 그대로 수행해 유저 의도를 저장 — 이후 권한만 허용하면 바로 알림 수신됨
- **권한 거부 + 서버 설정 ON 상태로 화면 진입 시**: 스위치는 서버 값(ON) 그대로 표시하되, 하단에 "기기 알림 설정이 꺼져 있어 알림을 받을 수 없어요" 안내 문구 + 설정 이동 텍스트 버튼 노출

### 9단계 — 검증

```bash
./gradlew ktlintFormat
./gradlew composeApp:assembleDebug
./gradlew test
```

## 결정 필요 / 열린 질문

1. **알림 설정 조회 API 부재**: 다중 기기·재설치 시 서버와 로컬 값이 어긋날 수 있음. 서버에 GET API 추가를 요청하거나, POST 등록 응답에 현재 설정값 포함을 제안할 만함. (당장은 로컬 저장으로 진행)
2. **APNs 키 등록 (외부 선행 조건)**: Apple Developer 콘솔에서 APNs 인증 키(.p8) 발급 후 Firebase 콘솔에 업로드 필요. 코드 구현과 병렬 진행 가능하며, 미등록 상태여도 앱 동작에는 지장 없음(iOS 토큰 발급만 실패).

## 확인된 사항

- **로그아웃/회원 탈퇴 시 토큰 삭제**: 서버에서 이미 처리하도록 구현돼 있음 — 클라이언트 추가 작업 불필요.
- **OS 권한 거부 상태의 스위치 UX**: 이번 범위에 포함 (8단계 참조).
