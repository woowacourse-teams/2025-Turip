---
topic: home
last_compiled: 2026-04-13
source_count: 16
status: active
---

# Home

## 목적 [coverage: high — 16 sources]

홈 탭의 진입 화면으로, 지역 카테고리 탐색과 인기 콘텐츠 노출, 키워드 검색 진입을 담당한다. 사용자는 국내/해외 버튼으로 지역 목록을 전환하고, 지역 칩을 탭하여 해당 지역의 콘텐츠 결과 화면으로 이동하거나, 검색 텍스트 필드에 키워드를 입력해 키워드 검색 화면으로 진입한다. 인기 북마크 콘텐츠 목록("users like")도 표시한다.

## 아키텍처 [coverage: high — 16 sources]

```
HomeScreen (Composable)
  ├─ HomeAppBar               → 로고 아이콘 표시
  └─ HomeScreenContent
       ├─ SearchTextField      → 키워드 입력 후 onSearchClick 콜백 호출
       ├─ UsersLikeList        → UsersLikeItem 목록 (인기 북마크 콘텐츠)
       ├─ RegionTypeButtons    → 국내/해외 선택 버튼
       └─ RegionList           → RegionItem(지역 칩) 목록

HomeViewModel (HiltViewModel)
  ├─ uiState: StateFlow<HomeUiState>
  ├─ uiEffect: Flow<HomeUiEffect>    (Channel.BUFFERED)
  ├─ loadContents()                  → 병렬 async로 인기 콘텐츠 + 지역 목록 조회
  └─ updateDomesticSelected(Boolean) → 국내/해외 전환 시 지역 목록 재조회
```

`HomeViewModel`은 `init` 블록에서 `loadContents()`를 호출하여 화면 최초 진입 시 데이터를 로드한다. `loadContents()` 내부에서 `contentRepository.loadPopularFavoriteContents()`와 `regionRepository.loadRegionCategories(isDomesticSelected)`를 `async`로 병렬 실행하고 두 결과를 `await()`한다.

## 의존 관계 [coverage: high — 16 sources]

```
HomeViewModel
  ├─ RegionRepository          (domain interface)
  │    └─ DefaultRegionRepository (data)
  │         └─ RegionRemoteDataSource → RegionService (Ktorfit)
  ├─ ContentRepository         (domain interface)
  │    └─ DefaultContentRepository (data)
  │         └─ ContentRemoteDataSource → ContentService (Ktorfit)
  └─ SessionManager            → TokenExpired 시 switchToGuest() 호출

HomeScreen
  └─ HomeNavigation.kt → 콜백 연결
       ├─ onSearchClick    → navigator.navigate(SearchNavKey(keyword))
       ├─ onRegionClick    → navigator.navigate(RegionResultNavKey(regionCategoryName))
       ├─ onContentClick   → navigator.navigate(TripDetailNavKey(contentId))
       └─ onNavigateToLoginScreen → navigator.goWithAllClear(LoginNavKey())
```

## API 표면 [coverage: high — 16 sources]

### `HomeViewModel` 공개 API

| 멤버 | 타입 | 설명 |
|------|------|------|
| `uiState` | `StateFlow<HomeUiState>` | 화면 상태 |
| `uiEffect` | `Flow<HomeUiEffect>` | 일회성 사이드 이펙트 |
| `loadContents()` | `fun` | 인기 콘텐츠 + 지역 목록 병렬 로드 (재시도 진입점) |
| `updateDomesticSelected(isDomesticSelected: Boolean)` | `fun` | 국내/해외 전환 시 지역 목록 재조회 |

### `RegionRepository`

```kotlin
suspend fun loadRegionCategories(isDomestic: Boolean): TuripResult<List<RegionCategory>>
// GET /v1/region-categories?isKorea={isDomestic}
```

### `ContentRepository` (Home 관련)

```kotlin
suspend fun loadPopularFavoriteContents(size: Int = 5): TuripResult<List<UsersLikeContent>>
// GET /v1/contents/popular?size={size}
```

## 데이터 [coverage: high — 16 sources]

### `HomeUiState`

```kotlin
@Stable
data class HomeUiState(
    val isLoading: Boolean,
    val regionCategories: List<RegionCategory>,
    val isDomesticSelected: Boolean,          // 기본값 true (국내)
    val usersLikeContents: List<UsersLikeContentModel>,
    val errorUiState: ErrorUiState,           // None / Network / Server
)
```

### `HomeUiEffect`

```kotlin
sealed interface HomeUiEffect {
    data object NavigateToLogin : HomeUiEffect
}
```

토큰 만료(`UiError.Global.TokenExpired`) 시 `SessionManager.switchToGuest()`를 호출한 뒤 이 이펙트를 전송한다.

### 도메인 모델

| 클래스 | 주요 필드 |
|--------|-----------|
| `RegionCategory` | `name: String`, `imageUrl: String` |
| `UsersLikeContent` | `content: Content`, `tripDuration: TripDuration` |
| `UsersLikeContentModel` (UI) | `content: Content`, `tripDuration: TripDurationModel` |

## 주요 결정사항 [coverage: high — 16 sources]

1. **병렬 로딩**: `loadContents()`에서 `async`/`await()`로 인기 콘텐츠와 지역 목록을 동시에 조회하여 총 로딩 시간을 줄인다.
2. **국내/해외 분리 요청**: `updateDomesticSelected()`는 지역 목록만 재조회하고 인기 콘텐츠는 재조회하지 않는다.
3. **키워드 상태는 Composable에서 관리**: `keyword` 상태는 `rememberSaveable`로 `HomeScreenContent` 내부에서 관리한다. ViewModel은 검색 실행 시 넘겨받는 키워드만 처리한다.
4. **세션 만료 처리**: `HomeViewModel`이 `SessionManager.switchToGuest()`를 직접 호출하여 로그인 화면 전환 전에 세션을 초기화한다.

## 주의사항 [coverage: medium — 8 sources]

- `loadContents()` 중 하나의 API만 실패해도 전체 에러 처리로 진입한다. 부분 성공 상태는 없다.
- `RegionService`의 쿼리 파라미터 이름은 `isKorea`다(코드상 `isDomestic`과 다름). 서버 API 스펙과 매핑에 주의해야 한다.
- `onNavigateToLoginScreen`은 `navigator.goWithAllClear(LoginNavKey())`로 연결되므로 모든 내비게이션 스택이 초기화된다.

## 출처 [coverage: high — 16 sources]

- [HomeScreen.kt](../../app/src/main/java/com/on/turip/ui/compose/home/HomeScreen.kt)
- [HomeViewModel.kt](../../app/src/main/java/com/on/turip/ui/compose/home/HomeViewModel.kt)
- [HomeUiState.kt](../../app/src/main/java/com/on/turip/ui/compose/home/HomeUiState.kt)
- [HomeUiEffect.kt](../../app/src/main/java/com/on/turip/ui/compose/home/HomeUiEffect.kt)
- [UsersLikeContentModel.kt](../../app/src/main/java/com/on/turip/ui/compose/home/model/UsersLikeContentModel.kt)
- [HomeAppBar.kt](../../app/src/main/java/com/on/turip/ui/compose/home/component/HomeAppBar.kt)
- [RegionTypeButtons.kt](../../app/src/main/java/com/on/turip/ui/compose/home/component/RegionTypeButtons.kt)
- [RegionList.kt](../../app/src/main/java/com/on/turip/ui/compose/home/component/RegionList.kt)
- [UsersLikeList.kt](../../app/src/main/java/com/on/turip/ui/compose/home/component/UsersLikeList.kt)
- [SearchTextField.kt](../../app/src/main/java/com/on/turip/ui/compose/home/component/SearchTextField.kt)
- [HomeNavKey.kt](../../app/src/main/java/com/on/turip/ui/compose/home/navigation/HomeNavKey.kt)
- [HomeNavigation.kt](../../app/src/main/java/com/on/turip/ui/compose/home/navigation/HomeNavigation.kt)
- [HomeNavKeyProvider.kt](../../app/src/main/java/com/on/turip/ui/compose/home/navigation/HomeNavKeyProvider.kt)
- [RegionRepository.kt](../../app/src/main/java/com/on/turip/domain/region/repository/RegionRepository.kt)
- [ContentRepository.kt](../../app/src/main/java/com/on/turip/domain/content/repository/ContentRepository.kt)
- [RegionService.kt](../../app/src/main/java/com/on/turip/data/region/service/RegionService.kt)
