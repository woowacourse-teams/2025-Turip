# iOS 딥링크 간헐적 오작동 수정 플랜

> Documents.md 6차 리뷰 반영 버전 (**설계 최종 승인**). **콜드 스타트 유실 + 재구독/동시 구독 중복 + ack 직후 재수신 + Unsupported의 Invitation 덮어쓰기 + 네비게이션 우선순위 경쟁 + 이전 generation effect 덮어쓰기**를 구조적으로 차단하는 것을 목표로 한다.

## 1. 배경 / 근본 원인

iOS에서 초대 링크 진입 시 간헐적으로 **홈 / 로그인 / TuripDetail**로 제각각 이동.

- **직접 원인**: iOS 콜드 스타트에서 딥링크 이벤트가 **간헐적으로 유실**됨.
- **왜 유실되나 (Race Condition)**:
  - iOS는 딥링크를 `MutableSharedFlow`로만 전달. (`IosDeepLinkBridge.kt`)
    ```kotlin
    private val iosDeepLinkEvents = MutableSharedFlow<String>(extraBufferCapacity = 1)
    // replay = 0 (기본값)
    ```
  - `extraBufferCapacity = 1`은 **"구독자가 없을 때 저장하는 공간이 아니다."** 구독자가 없는 상황에서는 replay 값만 보관되므로,
    `replay = 0`에서는 콜드 스타트 이벤트가 사라질 수 있다.
  - iOS `MainViewController.kt`는 `initialDeepLinkUrl = null`로 **항상 고정** → 콜드 스타트도 Flow에만 의존.
  - SwiftUI(`iOSApp.swift`)의 `onOpenURL` / `onContinueUserActivity`가 앱 실행 즉시 `emitIosDeepLink()`를 호출하는데,
    Compose의 `MainApp` 콜렉터(`LaunchedEffect { newDeepLinkFlow.collect { ... } }`)는 컴포지션 이후에야 구독 시작.
  - → emit이 구독보다 먼저면 유실 → splash가 `deepLinkUrl = null`로 세션 기반 기본 진입.

- **결과 매핑**:
  | 관찰 화면 | 원인 |
  |---|---|
  | 홈 | 딥링크 유실 + 세션 `Member` → `NavigateToMain` |
  | 로그인 | 딥링크 유실 + 세션 `Guest/Uninitialized` → `NavigateToLogin` |
  | TuripDetail (정상) | 딥링크가 구독 후 도착 → `goWithAllClear(InvitationEntryNavKey)` |

- **Android가 안정적인 이유**: `MainActivity`가 콜드 스타트 시 `intent.deepLinkUrl()`을 **동기적으로** 읽어
  `initialDeepLinkUrl`로 splash에 주입 → 콜드 스타트에 Flow 경쟁이 없음. Flow는 warm start(`onNewIntent`)에만 사용.

## 2. 목표 / 보장 범위

- iOS 콜드 스타트에서 딥링크 **유실 방지**.
- **동일 딥링크의 중복 offer는 pending 단계에서 병합**한다.
- **동시 구독**에서도 최종 네비게이션은 1회 (Store Collector 직렬화 + navigator idempotency 조합).
- **Unsupported가 유효한 Invitation pending을 밀어내지 않음** (유실의 다른 형태 차단).
- 보관형 전환으로 빨라진 딥링크가 splash 세션 네비게이션에 **덮어쓰이지 않도록** 우선순위 보장(Navigated/AlreadyHandling 모두).
- Android 라우팅 동작 **무회귀**.

> **전달 보장 정확한 의미** (정확히 한 번 전달 ❌):
> - **전달**: acknowledge 전까지 **at-least-once** — ⚠️ **iOS `DeepLinkStore` 기준에만 해당**.
>   Android passthrough source는 pending 저장/재전달을 하지 않으므로(기존 warm-start 전달 특성 유지) at-least-once 대상이 아니다.
>   **두 플랫폼 모두 navigator idempotency를 적용**한다.
> - **대기 이벤트**: latest-wins
> - **동시 소비**: 활성 Consumer 1개 (Store 직렬화)
> - **네비게이션 부작용**: navigator idempotency를 통한 **effective-once**
> - `Store`의 `Mutex`만으로 네비게이션 1회가 보장되는 것이 아니라, **Collector 직렬화 + `navigateInvitationIfNeeded()`** 조합으로 최종 1회 보장.

> **보장 범위 (중요)**: 재구독 보장은 **동일 navigation state 수명 내 Collector 재시작** 대상. 전체 `ComposeUIViewController` 재생성(navigator 상태 소실)은 §5.2 후속 옵션.

## 3. 수정 방향: 단일 소스(EventSource) + 1회 파싱 + CAS ack + 직렬화 Arbiter

### 3.1 왜 단순 `null` 리셋이 안 되는가

소비 후 무조건 `null`로 비우면 새 Race가 생긴다(A 처리 완료 시 뒤에 들어온 B까지 삭제). → **내가 처리한 이벤트와 동일할 때만** 원자적으로 제거한다(CAS).

### 3.2 타입 — 실행 가능한 route만 이벤트에 담는다 (강제 캐스팅 제거)

`DeepLinkEvent.route`를 `DeepLinkRoute`로 두면 향후 Source가 Unsupported를 흘려보낼 때 `as Invitation` 강제 캐스팅이 **런타임 크래시**가 된다. **이벤트에는 실행 가능한 route만 들어가도록 타입을 좁힌다.**

```kotlin
// commonMain
sealed interface ActionableDeepLinkRoute {
    val dedupeKey: String
    data class Invitation(val token: String) : ActionableDeepLinkRoute {
        // ⚠️ 호출마다 재계산 금지 → val로 1회 계산. 토큰 비노출 fingerprint.
        override val dedupeKey: String = "invitation:${sha256Fingerprint(token)}"
        override fun toString(): String = "Invitation(token=REDACTED)"   // data class 자동 toString 토큰 노출 차단
    }
}

sealed interface DeepLinkParseResult {
    data class Supported(val route: ActionableDeepLinkRoute) : DeepLinkParseResult
    data class Unsupported(val reason: UnsupportedReason) : DeepLinkParseResult   // raw URL 보관 불필요
}
fun parseDeepLinkSafely(url: String): DeepLinkParseResult   // 항상 반환, 예외 없음

data class DeepLinkEvent(
    val id: String,                     // 어떤 pending 이벤트를 소비했는지 식별 (UUID 등)
    val route: ActionableDeepLinkRoute, // 실행 가능 route만
) {
    val dedupeKey: String get() = route.dedupeKey   // 파생 속성 (중복 필드 X)
}

interface DeepLinkEventSource {
    val events: Flow<DeepLinkEvent>
    fun acknowledge(event: DeepLinkEvent): Boolean
}
```

- **`sha256Fingerprint`**: `secureFingerprint` 같은 모호한 이름 대신 **알고리즘이 드러나는 이름**으로 오용 방지.
  **UTF-8 입력 / 출력 형식(hex 등) 고정.** `String.hashCode()`는 충돌 위험으로 금지.

### 3.3 Store — Supported만 이벤트화 + Collector 직렬화 + CAS ack

```kotlin
class DeepLinkStore(
    private val parse: (String) -> DeepLinkParseResult = ::parseDeepLinkSafely,
    private val newEventId: () -> String = ::randomUuid,
) : DeepLinkEventSource {
    private val _pendingDeepLink = MutableStateFlow<DeepLinkEvent?>(null)
    private val collectorMutex = Mutex()

    // 활성 Collector 직렬화: 동시에 둘이 붙어도 한 번에 하나만 소비
    override val events: Flow<DeepLinkEvent> = flow {
        collectorMutex.lock()
        try {
            emitAll(_pendingDeepLink.filterNotNull())
        } finally {
            collectorMutex.unlock()
        }
    }

    fun offer(url: String) {
        when (val result = parse(url)) {        // ⚠️ 파싱 1회, 예외 없음 (Swift 진입점 보호)
            is DeepLinkParseResult.Supported -> {
                // ⚠️ id 생성은 update 블록 "밖"에서 (update 람다는 동시 갱신 시 여러 번 실행될 수 있음)
                val candidate = DeepLinkEvent(id = newEventId(), route = result.route)
                _pendingDeepLink.update { current ->
                    if (current?.dedupeKey == candidate.dedupeKey) current else candidate // 미소비 중복 병합
                }
            }
            // ⚠️ Unsupported는 pending에 저장하지 않음. 저장하면 latest-wins로 유효 Invitation을 덮어씀(유실의 다른 형태).
            is DeepLinkParseResult.Unsupported -> reportUnsupportedDeepLink(result.reason)
        }
    }

    override fun acknowledge(event: DeepLinkEvent): Boolean =
        _pendingDeepLink.compareAndSet(expect = event, update = null) // A 처리 중/후 B·A2가 와도 안 지움
}
```

- 사용자 오류 화면이 필요해지면 최소한 `pending Invitation > 새 Unsupported` 우선순위를 둔다(이번엔 입력 폐기).
- 멀티 Scene 실제 지원 시 직렬화 대신 `Pending/InFlight + tryClaim(event, consumerId)`.

### 3.4 iOS 브리지 — 프로세스 수명 싱글턴 (필수)

콜드 스타트 보관이 성립하려면 Store가 **`MainViewController` 안에서 생성되면 안 된다.** Swift가 먼저 offer한 뒤 Controller가 새 Store를 만들면 원래 문제를 재현.

```kotlin
// iosMain — 프로세스 수명 싱글턴, Swift 호출을 store로 넘기는 얇은 어댑터
internal object IosDeepLinkBridge {
    private val store = DeepLinkStore(parse = ::parseDeepLinkSafely, newEventId = ::randomUuid)
    val eventSource: DeepLinkEventSource get() = store
    fun offer(url: String) = store.offer(url)
}
fun emitIosDeepLink(url: String) = IosDeepLinkBridge.offer(url)
```

- `MainViewController`는 **반드시 동일한 `IosDeepLinkBridge.eventSource`** 를 `MainApp`에 전달.

### 3.5 MainApp — EventSource 자체 전달 + 강제 캐스팅 없는 소비 + 원자적 idempotency

```kotlin
@Composable
fun MainApp(
    initialDeepLinkUrl: String?,
    deepLinkEventSource: DeepLinkEventSource,   // Flow/ack 따로 X
) {
    // ⚠️ startup 수명: MainApp이 재생성 안 돼도 owner 잔존으로 다음 Splash가 영구 차단되지 않도록 generation 키 사용 (§5.1)
    val startupNavigationArbiter = remember(startupGenerationId) { StartupNavigationArbiter() }

    LaunchedEffect(deepLinkEventSource, startupNavigationArbiter) {  // Arbiter 변경 시 Collector도 재구독
        deepLinkEventSource.events.collect { event ->               // collectLatest 금지
            // route는 ActionableDeepLinkRoute라 강제 캐스팅 없음
            val result = startupNavigationArbiter.navigateByDeepLink {
                when (val route = event.route) {
                    is ActionableDeepLinkRoute.Invitation ->
                        navigator.navigateInvitationIfNeeded(dedupeKey = event.dedupeKey, token = route.token)
                }
            }
            when (result) {
                Navigated, AlreadyHandling -> deepLinkEventSource.acknowledge(event)
            }
        }
    }
    // ...
}
```

- **ack 직후 동일 링크 재수신** 중복은 `navigateInvitationIfNeeded`의 의미 기반 idempotency(`Navigated/AlreadyHandling`)로 막는다. Navigator는 Arbiter를 직접 알 필요 없음.
- **handling 수명**: InvitationEntry 진입 시 시작 / TuripDetail 전환돼도 같은 초대 흐름이면 유지 / 그 흐름이 back stack에서 제거되면 해제 / 해제 후 동일 링크 재탭 허용. → "현재 화면이 InvitationEntry인지"만 검사 금지(TuripDetail 전환 직후 중복 발생).
- **Android**는 별도 `passthrough` `DeepLinkEventSource`(warm-start Flow 래핑, `acknowledge = { true }`). Activity 필드/`remember`로 고정(익명 인스턴스 매번 생성 → LaunchedEffect 재시작 금지).

### 3.6 직렬화 Arbiter — generic 결과 반환, 실제 navigator 변경 지점

```kotlin
private enum class NavigationOwner { None, Session, DeepLink }

class StartupNavigationArbiter {                 // 앱 싱글턴 X, 1회 startup 수명
    private val mutex = Mutex()
    private var owner = NavigationOwner.None

    suspend fun navigateBySession(navigate: () -> Unit): Boolean = mutex.withLock {
        if (owner != NavigationOwner.None) return@withLock false
        owner = NavigationOwner.Session
        try { navigate(); true } catch (t: Throwable) { owner = NavigationOwner.None; throw t }
    }

    suspend fun <T> navigateByDeepLink(navigate: () -> T): T = mutex.withLock {
        val prev = owner
        owner = NavigationOwner.DeepLink                 // 호출 즉시 선점 (결과와 무관 → AlreadyHandling도 우선권)
        try { navigate() } catch (t: Throwable) { owner = prev; throw t }
    }
}
```

- **owner 선점이 navigate 결과보다 먼저** → `AlreadyHandling`(추가 네비게이션 없음)이어도 DeepLink가 우선권을 가져가 늦은 Splash가 초대 화면을 덮지 못함.
- **`navigate` 블록 계약**: suspend 금지 / coroutine launch 금지 / 다른 dispatcher post 후 즉시 반환 금지 / 실제 navigation state를 **동기 변경**.
- **Main thread 계약**: `Mutex`는 순서만 직렬화 → 호출은 **Main/UI dispatcher**, navigate 블록은 동기 변경.
- **실제 navigator 변경 지점 적용** — ViewModel이 `NavigateToMain` Effect만 발행하고 UI가 늦게 소비하면 무력화됨. splash 홈/로그인도 **navigator 실제 호출 UI 지점**에서 `navigateBySession { navigator.goWithAllClear(...) }`.
- 결과: Session 먼저 락 → 이후 DeepLink가 덮음 / DeepLink 먼저 락 → 이후 Session **차단** → **최종 화면 항상 DeepLink 우선**, 실패 시 owner 복원.
- 네비게이션 API가 본질적으로 비동기면 `Mutex`보다 **단일 `NavigationCoordinator` actor**.

> ⚠️ **이전 generation effect 덮어쓰기 가드 (§5.1과 함께)**: Splash 세션 navigation은 **generation 태그**를 달아야 한다.
> 그렇지 않으면 이전 startup에서 발행된 `NavigateToMain`이 새 Arbiter에서 처리되며 딥링크를 덮는 변형 Race가 생긴다.
> ```kotlin
> data class NavigateBySession(val generationId: Long, val destination: Destination)
>
> // 실제 navigator consumer:
> if (effect.generationId != currentStartupGenerationId) return@collect   // 이전 generation effect 폐기
> startupNavigationArbiter.navigateBySession { navigator.goWithAllClear(effect.destination) }
> ```

### 3.7 대안 기각 사유

| 방안 | 기각 사유 |
|---|---|
| `SharedFlow(replay = 1)` | 재구독 시 재실행. A 처리 중 B 수신 후 `resetReplayCache()` 호출 시 B까지 삭제. `resetReplayCache()`는 `ExperimentalCoroutinesApi`. 결국 별도 소비 상태 관리 필요 → 더 복잡 |
| `Channel.CONFLATED` | `receiveAsFlow()`는 요소 수신 직후 Collector 취소 시 **처리 전 요소가 사라질 수 있음**(공식 문서). 같은 종류의 유실 구간 재도입 |

## 4. 공유 코드 / Android 영향 최소화

- `MainApp`은 `DeepLinkEventSource`만 받는다(타입 안전).
- **Android**: 콜드 스타트 `initialDeepLinkUrl` 경로 유지. warm-start Flow를 감싸는 **passthrough `DeepLinkEventSource`** (`acknowledge = { true }`), Activity 필드/`remember`로 고정. 라우팅 동작 불변.
- **iOS**: `IosDeepLinkBridge.eventSource` 그대로 전달. iosMain은 얇은 어댑터.
- **순수 상태 로직(`DeepLinkStore`, `StartupNavigationArbiter`, `parseDeepLinkSafely`)은 commonMain에 두고 commonTest로 검증.**

## 5. 변경 대상 파일 / 인스턴스 수명 / 보장 범위

- (신규, commonMain) `ActionableDeepLinkRoute`, `DeepLinkParseResult`, `UnsupportedReason`, `parseDeepLinkSafely`, `sha256Fingerprint`, `DeepLinkEvent`, `DeepLinkEventSource`, `DeepLinkStore`, `StartupNavigationArbiter`, `navigateInvitationIfNeeded`(결과 enum `Navigated/AlreadyHandling`).
- `composeApp/src/iosMain/.../IosDeepLinkBridge.kt` — **프로세스 수명 싱글턴 object**, `emitIosDeepLink` → `store.offer`.
- `composeApp/src/iosMain/.../MainViewController.kt` — `IosDeepLinkBridge.eventSource` 전달.
- `feature/main/.../MainApp.kt` — `DeepLinkEventSource` 시그니처, Arbiter generation 키 `remember`, 강제 캐스팅 없는 소비, Arbiter는 실제 navigator 호출 감쌈.
- `composeApp/src/androidMain/.../MainActivity.kt` — warm-start Flow를 passthrough source로(콜드 경로 유지, 필드/remember 고정).
- `feature/splash/.../SplashViewModel.kt`(+ Splash UI) — 홈/로그인 이동을 **실제 navigator 호출 지점**에서 `arbiter.navigateBySession { }`으로.
- (검토) `iosApp/iosApp/iOSApp.swift` — emit 경로 현행 유지 가능.

### 5.1 인스턴스 수명 계약 (코드로 구현)

- **Arbiter의 startup 수명을 코드로 보장**: `MainApp`이 로그아웃/세션 초기화 후에도 유지되면 owner가 `Session`/`DeepLink`로 남아 다음 Splash 네비게이션을 **영구 차단**할 수 있다.
  - MainApp이 매 startup마다 재생성된다는 보장이 없다면 **startup generation** 사용:
    ```kotlin
    val startupNavigationArbiter = remember(startupGenerationId) { StartupNavigationArbiter() }
    LaunchedEffect(deepLinkEventSource, startupNavigationArbiter) { ... }   // Arbiter 변경 시 재구독
    ```
  - Splash 실제 navigator consumer에도 **정확히 같은 Arbiter**를 전달.
  - 반대로 "Splash는 프로세스당 1회 실행 + MainApp도 함께 재생성"이 앱 구조상 보장된다면, **그 불변 조건을 문서/테스트에 명시**하고 generation 키를 생략해도 됨.
- **`startupGenerationId` 생성·갱신 주체 계약 (반드시 확정)**:
  - 한 번의 Splash 시작~종료까지 **동일 generation 유지**.
  - **새 startup / session initialization 시작 시에만 증가**.
  - recomposition, 딥링크 수신, 화면 전환만으로는 **변경하지 않음**.
  - 이전 generation의 **미소비 Splash navigation effect는 폐기**(§3.6의 `generationId` 가드). 그렇지 않으면 이전 startup의 `NavigateToMain`이 새 Arbiter에서 처리되며 딥링크를 덮는 변형 Race 발생.
- **iOS EventSource**: `IosDeepLinkBridge` 프로세스 싱글턴.
- **Android passthrough source**: Activity 필드/`remember` 고정.
- 익명 `DeepLinkEventSource`를 Composable 호출마다 새로 만들면 `LaunchedEffect`가 계속 재시작되므로 금지.

### 5.2 보장 범위 / 후속

- **보장**: 동일 navigation state 수명 내 Collector 재시작(LaunchedEffect 재시작) — 이벤트는 이미 ack, navigator에 InvitationEntry 유지.
- **미보장**: 전체 `ComposeUIViewController` 재생성으로 navigator 상태까지 소실되는 경우.
  필요 시 후속 옵션 중 택1: ①navigator 상태 복원 ②딥링크를 Controller보다 긴 수명에 저장 ③destination commit 이후 ack ④새 Controller에 초기 딥링크 재주입 복구 경로.
- 현재의 "라우터 인수 직후 ack"는 **navigator 상태가 Collector보다 오래 유지된다는 가정** 아래에서만 정확함.

## 6. 검증 계획

### 6.1 자동 테스트 — commonTest (순수 상태 로직)

- [ ] Collector 생성 **전** offer → 구독 후 반드시 전달 (콜드 스타트 유실 회귀)
- [ ] A 처리 중 B emit → `ack(A)`가 B를 삭제하지 않음 (CAS)
- [ ] A ack 후 동일 dedupeKey의 A2 offer → 오래된 ack(A)가 A2를 제거하지 않음
- [ ] acknowledge 후 Collector 재생성 → 재전달 없음
- [ ] **acknowledge 전 Collector 취소 → 다음 Collector에 동일 pending 재전달** (at-least-once, 의도된 동작)
- [ ] 같은 dedupeKey 연속 offer(미소비) → pending 이벤트 하나 (병합)
- [ ] 두 Collector가 동시에 구독해도 한 Collector만 소비 (Store 직렬화)
- [ ] 첫 Collector 취소 → 두 번째 lock 획득 → 새 offer → 두 번째만 수신
- [ ] **Unsupported offer는 기존 Invitation pending을 덮어쓰지 않음** (입력 단계 폐기)
- [ ] 같은 dedupeKey가 ack 직후 재수신돼도 중복 네비게이션 없음 (handling 수명 + idempotency)
- [ ] A 처리 중 B, C 순서 offer → A 다음 C 처리, B는 latest-wins 정책상 폐기
- [ ] Splash 실제 navigator 실행 순서를 교차시켜도 DeepLink가 최종 화면 (`AlreadyHandling` 포함 owner 선점)
- [ ] 네비게이션 함수 예외 시 Arbiter owner는 이전 상태로 복원되고, 이벤트는 acknowledge되지 않아 pending 유지. 자동 재시도는 별도 보장하지 않음.
- [ ] dedupeKey/toString에 raw token/URL 미노출 (sha256 fingerprint·redacted 검증)
- [ ] **이전 startup generation의 세션 navigation effect가 새 generation에서 폐기됨** (generationId 가드)
- [ ] **Android passthrough source는 iOS Store의 at-least-once 계약 대상이 아님** (재전달 미보장 검증/문서화)

### 6.2 자동 테스트 — iosTest / iosSimulatorArm64Test

- [ ] **`MainViewController` 생성 전 `IosDeepLinkBridge.offer` → 이후 동일 Store에서 전달** (프로세스 싱글턴)

### 6.3 수동 매트릭스 (iOS 실기기 / 카카오톡·사파리·기타 브라우저)

- [ ] 콜드 스타트 × Member → TuripDetail (10회 반복, 유실 0)
- [ ] 콜드 스타트 × Guest → InvitationEntry → 로그인 → 검증 후 TuripDetail
- [ ] Warm start(백그라운드 후 재탭) → 정상 진입
- [ ] 같은 링크 연속 탭(ack 전/후, TuripDetail 전환 직후 포함) → 중복 네비게이션 없음
- [ ] 유효하지 않은/만료 토큰 → 기존 에러 분기

### 6.4 빌드 명령

```bash
./gradlew ktlintFormat
./gradlew composeApp:assembleDebug                      # Android/common 검증
# ⚠️ assembleDebug는 iosMain(Kotlin/Native)을 컴파일하지 않음. 먼저 task 존재 확인:
./gradlew :composeApp:tasks --all | grep -i ios
./gradlew :composeApp:compileKotlinIosSimulatorArm64    # iosMain 컴파일 (targets: iosArm64, iosSimulatorArm64)
./gradlew :composeApp:iosSimulatorArm64Test             # (task 존재 시) iosTest 실행
xcodebuild ...                                          # 실제 iosApp 빌드
```

> CI에 iOS 컴파일/테스트 task 추가 권장 (현재 `assembleDebug`만으로는 `IosDeepLinkBridge.kt` 컴파일 미보장).

## 7. 구현 승인 조건 (Definition of Done)

> 설계는 **최종 승인**됨. 구현 시 아래 2가지(수명/계약 봉합)를 **반드시 확정**한다.
> - **(A) `startupGenerationId` 생성·갱신 주체 확정** — §5.1 계약 + §3.6 generationId 가드(이전 generation effect 폐기).
> - **(B) at-least-once 보장 범위 = iOS `DeepLinkStore`로 한정** — Android passthrough는 재전달 미보장, 양 플랫폼 idempotency 적용(§2).

1. **실행 가능한 route만 `DeepLinkEvent`에 들어가도록 타입 제한**(`ActionableDeepLinkRoute` + `DeepLinkParseResult`) — 강제 캐스팅/런타임 크래시 제거.
2. **Arbiter의 startup 수명과 재생성 조건을 코드로 구현**(generation 키 또는 불변 조건 명시) + **`startupGenerationId` 갱신 주체 확정** + **이전 generation Splash effect 폐기 가드** — owner 잔존 영구 차단 / 변형 Race 방지.
3. **전달 보장을 at-least-once(iOS Store 한정) + idempotent navigation(양 플랫폼)으로 정확히 기술**(정확히 한 번 전달 아님).
4. `MainApp`에 **`DeepLinkEventSource` 자체 전달** + 안정적 동일 인스턴스(§5.1).
5. **Store 레벨 단일 활성 Collector 직렬화**(+ navigator idempotency로 최종 1회) — 또는 멀티 Scene 시 `tryClaim`.
6. **iOS Store 프로세스 수명 싱글턴**, **Unsupported 입력 폐기**, **dedupeKey 파생 + sha256 fingerprint(1회 계산, UTF-8/출력형식 고정) + redacted toString**.
7. **Arbiter `navigateByDeepLink` generic 결과 반환**(Navigated/AlreadyHandling 모두 owner 선점), 실제 navigator 지점, Main dispatcher·동기 navigate 계약 + **파싱 1회** + handling 수명 정의.
8. iosMain 컴파일 통과 + §6.1/§6.2 자동 테스트 포함(이전 generation effect 폐기, Android 비대상 검증 포함).

## 8. 범위 외 (후속 과제)

- splash 단일 경로 통합(딥링크/세션 네비게이션 일원화) — 이번엔 Arbiter 가드만, 통합은 분리.
- 전체 `ComposeUIViewController` 재생성 복구 경로(§5.2).
- 멀티 윈도우/다중 Scene 지원(`tryClaim` 기반).
- Unsupported 딥링크 사용자 오류 화면(필요 시 `pending Invitation > Unsupported` 우선순위와 함께).
- `turip-invitation` 정적 사이트(AASA/assetlinks/index.html) — 현재 분석상 정상.
