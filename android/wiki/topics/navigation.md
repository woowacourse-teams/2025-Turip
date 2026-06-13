---
topic: navigation
last_compiled: 2026-04-13
source_count: 14
status: active
---

# Navigation

## 목적 [coverage: high — 14 sources]

앱 전체의 화면 전환을 단일 Navigation3(`androidx.navigation3`) 기반 상태 관리자로 통제한다. Jetpack Navigation Component를 사용하지 않고, Navigation3의 `NavBackStack`과 커스텀 `Navigator` 클래스를 조합하여 TopLevel 탭별 독립 백스택을 구현한다. 딥링크 진입, 로그인/세션 전환처럼 히스토리를 완전히 초기화해야 하는 시나리오는 `goWithAllClear`로 처리한다.

## 아키텍처 [coverage: high — 14 sources]

```
MainActivity
  └─ MainApp (root Composable)
       ├─ rememberNavigationState()       → NavigationState
       ├─ Navigator(state)                → 스택 조작 명령어 집합
       ├─ rememberTuripAppState()         → TuripAppState (스낵바·시스템바 포함)
       ├─ NavDisplay(entries)             → 현재 스택의 NavEntry 목록 렌더링
       ├─ TuripNavigationBar             → 탭 선택 시 navigator.navigate() 호출
       └─ ExitConfirmationHandler        → 루트 화면에서 두 번 뒤로 누르면 앱 종료
```

**스택 구조**

| 구분 | 타입 | 역할 |
|------|------|------|
| `topLevelStack` | `NavBackStack<NavKey>` | 현재 선택된 TopLevel 탭의 순서를 유지 |
| `subStacks` | `Map<NavKey, NavBackStack<NavKey>>` | 탭별로 독립 유지되는 서브 스택 (홈/내 튜립/마이페이지 각각) |

`NavigationState.toEntries()` 확장 함수가 `topLevelStack`을 기준으로 각 서브스택의 `NavEntry` 목록을 평탄화하여 `NavDisplay`에 전달한다. 각 `NavEntry`에는 `rememberSaveableStateHolderNavEntryDecorator`와 `rememberViewModelStoreNavEntryDecorator`가 적용되어 탭 전환 시에도 상태와 ViewModel이 유지된다.

**화면 등록 흐름**

각 피처는 `NavKeyProvider` 인터페이스를 구현한 클래스를 제공한다. `NavigationModule`이 Hilt `@IntoSet` 멀티바인딩으로 모든 `NavKeyProvider`를 `List<NavKeyProvider>`로 조립하고, `appScreens()` 확장 함수가 동일 목록으로 `EntryProviderScope`에 화면을 등록한다.

## 의존 관계 [coverage: high — 14 sources]

```
Navigator
  └─ NavigationState
       └─ NavBackStack (androidx.navigation3)

MainApp
  ├─ Navigator
  ├─ NavigationState (via rememberNavigationState)
  ├─ TuripAppState (via rememberTuripAppState)
  ├─ SavedStateConfigurationProvider  (Hilt inject)
  │    └─ List<NavKeyProvider>  (Hilt @IntoSet multibinding)
  ├─ NavDisplay (androidx.navigation3.ui)
  ├─ TuripNavigationBar
  └─ ExitConfirmationHandler

NavigationModule (Hilt, ActivityComponent)
  └─ binds 11 NavKeyProvider implementations:
       BookmarkNavKeyProvider, HomeNavKeyProvider, InvitationEntryNavKeyProvider,
       LoginNavKeyProvider, MyPageNavKeyProvider, SplashNavKeyProvider,
       SearchNavKeyProvider, RegionResultNavKeyProvider, TripDetailNavKeyProvider,
       MyTuripNavKeyProvider, TuripDetailNavKeyProvider
```

## API 표면 [coverage: high — 14 sources]

### `Navigator`

| 메서드 | 동작 규칙 |
|--------|-----------|
| `navigate(key)` | 현재 TopLevel과 동일 → 서브스택 루트만 남기고 정리. TopLevel 키 → 해당 탭 전환. 그 외 → 현재 서브스택 끝으로 push (중복 키는 제거 후 재추가). |
| `replace(key)` | TopLevel 키 → 탭 전환. 그 외 → 서브스택 마지막 항목 제거 후 key push. |
| `goBack()` | startKey 또는 현재 TopLevel 루트면 아무 동작 없음. 그 외 → 서브스택 마지막 항목 제거. |
| `goWithAllClear(key, parentTopLevelKey)` | 모든 스택 초기화 후 key로 이동. key가 TopLevel이면 해당 탭을 루트로 설정. 아닌 경우 `parentTopLevelKey`의 서브스택에 key를 추가. |

### `NavKeyProvider` 인터페이스

각 피처는 이 인터페이스를 구현하고 Hilt `@IntoSet`으로 바인딩하면 네비게이션에 자동 등록된다.

```kotlin
interface NavKeyProvider {
    fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator)
}
```

### `NavigationState`

| 프로퍼티 | 설명 |
|----------|------|
| `startKey` | 앱 시작 라우트 (= `HomeNavKey`). 이 키에서 뒤로 가면 앱 종료 확인 |
| `topLevelStack` | 탭 선택 순서 스택 |
| `subStacks` | 탭별 서브 스택 Map |
| `currentTopLevelKey` | `topLevelStack.last()` (derived) |
| `currentKey` | `currentSubStack.last()` (derived) |
| `currentSubStack` | `subStacks[currentTopLevelKey]` |

## 데이터 [coverage: high — 14 sources]

**TopLevel 라우트 — `TopLevel.routes`**

| NavKey | 아이콘 | 레이블 |
|--------|--------|--------|
| `HomeNavKey` (data object) | `Icons.Default.Home` | `bottom_navigation_home` |
| `MyTuripNavKey` (data object) | `Icons.Default.Folder` | `bottom_navigation_my_turip` |
| `MyPageNavKey` (data object) | `Icons.Default.Person` | `bottom_navigation_my_page` |

**초기 진입**: `MainApp`은 `initialEntryKey = SplashNavKey`로 시작한다. 스플래시→로그인→홈 전환은 각각 `navigator.replace()` 또는 `navigator.goWithAllClear()`로 처리된다.

**딥링크**: `newDeepLinkFlow: Flow<String>`을 `LaunchedEffect`로 수집하여, 딥링크 URL을 `InvitationEntryNavKey(url)`에 담아 `navigator.goWithAllClear()`로 모든 히스토리를 초기화한 뒤 초대 화면으로 진입한다.

**NavKey 직렬화**: 각 NavKey는 `@Serializable`을 붙인 data object 또는 data class다. `SavedStateConfigurationProvider`가 `polymorphic(NavKey::class)` 블록에서 피처별 NavKey를 등록해 프로세스 재생성 시에도 백스택이 복원된다.

## 주요 결정사항 [coverage: high — 14 sources]

1. **Navigation Component 미사용**: Navigation3(`androidx.navigation3`)을 직접 채택하여 중첩 그래프 없이 탭별 독립 `NavBackStack`으로 백스택을 관리한다.
2. **탭 독립 백스택**: 탭 전환 시 이전 탭의 서브스택이 그대로 보존된다. 같은 탭을 다시 탭하면 루트만 남긴다.
3. **`@Stable` 마킹**: `NavigationState`와 `TuripAppState` 모두 `@Stable`로 표시하여 불필요한 recomposition을 방지한다.
4. **피처별 `NavKeyProvider`**: 피처가 직접 라우트와 화면을 Hilt를 통해 등록하므로, 앱 모듈은 피처 내부를 알 필요 없다. 신규 피처 추가 시 `NavigationModule`에 `@Binds @IntoSet` 한 줄만 추가한다.
5. **ViewModel 범위**: `rememberViewModelStoreNavEntryDecorator`를 적용해 각 `NavEntry`에 ViewModel이 귀속된다. 탭 전환 후 돌아와도 ViewModel이 유지된다.

## 주의사항 [coverage: medium — 8 sources]

- `goBack()`은 `startKey`(HomeNavKey)나 TopLevel 루트에서는 동작하지 않는다. `ExitConfirmationHandler`가 `BackHandler`로 종료 확인 스낵바를 표시한다.
- `goWithAllClear(key, parentTopLevelKey)`에서 `parentTopLevelKey`가 `topLevelKeys`에 포함되지 않으면 자동으로 `startKey`(HomeNavKey)로 대체된다.
- `NavigationState.currentTopLevelKey`와 `currentKey`는 스택이 비어 있으면 `error()`를 던진다. `clearAllStacks()` 직후 새 키를 추가하기 전 접근하면 크래시가 발생한다.
- `NavKeyProvider.registerNavKeys()`를 `NavigationModule`에 바인딩하지 않으면 해당 피처의 NavKey가 `SavedStateConfiguration`에 등록되지 않아 프로세스 재생성 시 복원에 실패한다.

## 출처 [coverage: high — 14 sources]

- [Navigator.kt](../../app/src/main/java/com/on/turip/navigation/Navigator.kt)
- [NavigationState.kt](../../app/src/main/java/com/on/turip/navigation/NavigationState.kt)
- [NavKeyProvider.kt](../../app/src/main/java/com/on/turip/navigation/NavKeyProvider.kt)
- [TopLevel.kt](../../app/src/main/java/com/on/turip/ui/compose/main/navigation/TopLevel.kt)
- [AppScreens.kt](../../app/src/main/java/com/on/turip/ui/compose/main/navigation/AppScreens.kt)
- [TuripAppState.kt](../../app/src/main/java/com/on/turip/ui/compose/main/navigation/TuripAppState.kt)
- [SavedStateConfigurationProvider.kt](../../app/src/main/java/com/on/turip/ui/compose/main/navigation/SavedStateConfigurationProvider.kt)
- [MainApp.kt](../../app/src/main/java/com/on/turip/ui/compose/main/MainApp.kt)
- [TuripNavigationBar.kt](../../app/src/main/java/com/on/turip/ui/compose/main/component/TuripNavigationBar.kt)
- [ExitConfirmationHandler.kt](../../app/src/main/java/com/on/turip/ui/compose/main/component/ExitConfirmationHandler.kt)
- [NavigationModule.kt](../../app/src/main/java/com/on/turip/di/NavigationModule.kt)
- [HomeNavKey.kt](../../app/src/main/java/com/on/turip/ui/compose/home/navigation/HomeNavKey.kt)
- [HomeNavigation.kt](../../app/src/main/java/com/on/turip/ui/compose/home/navigation/HomeNavigation.kt)
- [HomeNavKeyProvider.kt](../../app/src/main/java/com/on/turip/ui/compose/home/navigation/HomeNavKeyProvider.kt)
