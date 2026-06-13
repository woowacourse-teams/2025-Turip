# Dialog Android Client — Developer Guide

## 1. 아키텍처 패턴

**Kotlin Multiplatform (KMP) + Clean Architecture + MVIA (Model-View-Intent-Action)**

### 디렉토리 구조

```
mobile/
├── composeApp/                    # 앱 엔트리포인트 & DI 조립
├── core/
│   ├── common/                    # NetworkError, 유틸, 확장함수
│   ├── data/                      # Repository 구현체 (XxxDefaultRepository)
│   ├── designsystem/              # 디자인 토큰, 테마, 공통 컴포넌트
│   ├── domain/                    # Repository 인터페이스, UseCase
│   ├── local/                     # Room DB, DataStore
│   ├── model/                     # 공유 도메인 모델
│   ├── navigation/                # NavKeyProvider 인터페이스
│   ├── network/                   # Datasource 인터페이스/구현, DTO
│   └── ui/                        # BaseViewModel, 공통 UI 컴포넌트
├── feature/
│   ├── feature1/
│   │   ├── api/                   # NavKey만 공개 (다른 feature가 참조)
│   └── └── impl/                  # Screen, ViewModel, DI, Component
│   
│   
│   
│   
│   
│  
│   
└── build-logic/convention/        # 공유 Gradle 컨벤션 플러그인
```

### 레이어 의존 방향

```
Screen → ViewModel → UseCase → Repository(interface) ← DefaultRepository → Datasource → HTTP
                                     ↑
                               core:domain          core:data          core:network
```

**규칙**: feature impl끼리는 직접 의존 금지. 다른 feature로 이동할 때는 해당 feature의 `api` 모듈의 NavKey만 사용.

---

## 2. 네이밍 컨벤션

### 클래스/파일

| 종류 | 패턴 | 예시 |
|------|------|------|
| Composable 화면 | `XxxScreen.kt` | `DiscussionListScreen.kt` |
| ViewModel | `XxxViewModel.kt` | `DiscussionListViewModel.kt` |
| 상태 | `XxxState.kt` | `DiscussionListState.kt` |
| 인텐트 | `XxxIntent.kt` | `DiscussionListIntent.kt` |
| 이펙트 | `XxxEffect.kt` | `DiscussionListEffect.kt` |
| UI 모델 | `XxxUiModel.kt` | `DiscussionUiModel.kt` |
| Repository 인터페이스 | `XxxRepository.kt` | `DiscussionRepository.kt` |
| Repository 구현체 | `XxxDefaultRepository.kt` | `DiscussionDefaultRepository.kt` |
| Datasource 인터페이스 | `XxxDatasource.kt` | `DiscussionDatasource.kt` |
| Datasource 구현체 | `XxxRemoteDatasource.kt` | `DiscussionRemoteDatasource.kt` |
| DI 모듈 | `XxxModule.kt` | `DiscussionListModule.kt` |
| NavKey | `XxxNavKey.kt` | `DiscussionListNavKey.kt` |
| 컴포넌트 | `XxxTopAppBar.kt`, `XxxCard.kt` | `DiscussionCard.kt` |

### 함수

- 상태 변환: `updateState { copy(...) }`
- 이펙트 발행: `emitEffect(XxxEffect.Yyy)`
- 인텐트 처리: `handle[IntentName]()`, `toggle[Property]()`
- 매핑: `toDomain()`, `toUiModel()`, `toQuery()`
- Boolean: `is[State]`, `should[Action]`, `has[Property]`

---

## 3. 자주 쓰는 패턴

### MVIA 패턴 (모든 화면 동일 구조)

```kotlin
// State — @Immutable data class, computed 프로퍼티 포함
@Immutable
data class DiscussionListState(
    val discussions: ImmutableList<DiscussionUiModel> = persistentListOf(),
    val isLoading: Boolean = false,
    val isFetched: Boolean = false,
) : UiState {
    // 계산된 프로퍼티는 State 안에 정의
    val filteredDiscussions: ImmutableList<DiscussionUiModel> =
        discussions.filter { ... }.toImmutableList()

    val shouldShowEmptyView: Boolean = isFetched && filteredDiscussions.isEmpty()
}

// Intent — sealed interface
sealed interface DiscussionListIntent : UiIntent {
    data class ClickTrackFilter(val track: TrackUiModel) : DiscussionListIntent
    data object RefreshList : DiscussionListIntent
    data object LoadNextPage : DiscussionListIntent
}

// Effect — 1회성 이벤트 (네비게이션, 스크롤, 스낵바)
sealed interface DiscussionListEffect : UiEffect {
    data class ShowSnackbar(val message: String) : DiscussionListEffect
    data object ScrollToTop : DiscussionListEffect
    data class SetFabVisible(val isVisible: Boolean) : DiscussionListEffect
}
```

### BaseViewModel

```kotlin
class DiscussionListViewModel(
    private val discussionRepository: DiscussionRepository,
) : BaseViewModel<DiscussionListIntent, DiscussionListState, DiscussionListEffect>(
    DiscussionListState()  // 초기 상태
) {
    override fun onIntent(intent: DiscussionListIntent) {
        when (intent) {
            is DiscussionListIntent.ClickTrackFilter -> toggleTrackFilter(intent.track)
            is DiscussionListIntent.RefreshList -> refreshListImmediate()
            is DiscussionListIntent.LoadNextPage -> fetchDiscussions()
        }
    }

    private fun fetchDiscussions() {
        viewModelScope.launch {
            discussionRepository
                .getDiscussions(...)
                .onSuccess { handleFetchDiscussionsSuccess(it) }
                .onFailure { handleFetchDiscussionsFailure(it) }
        }
    }

    private fun handleFetchDiscussionsSuccess(result: DiscussionCatalogCursorPage) {
        updateState {
            copy(
                discussions = result.discussionCatalog
                    .map { it.toUiModel() }
                    .toImmutableList(),
                isFetched = true,
            )
        }
        emitEffect(DiscussionListEffect.ScrollToTop)
    }
}
```

### Screen (Composable)

```kotlin
@Composable
fun DiscussionListScreen(
    viewModel: DiscussionListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DiscussionListEffect.ShowSnackbar -> { /* snackbar */ }
                DiscussionListEffect.ScrollToTop -> listState.animateScrollToItem(0)
            }
        }
    }

    DiscussionListContent(
        state = uiState,
        onIntent = viewModel::onIntent,
    )
}
```

### Repository Pattern (Result<T> 반환)

```kotlin
// core:domain — 인터페이스
interface DiscussionRepository {
    suspend fun getDiscussions(
        discussionCriteria: DiscussionCriteria,
        cursor: String?,
        size: Int,
    ): Result<DiscussionCatalogCursorPage>
}

// core:data — 구현체
class DiscussionDefaultRepository(
    private val discussionDatasource: DiscussionDatasource,
) : DiscussionRepository {
    override suspend fun getDiscussions(...): Result<DiscussionCatalogCursorPage> =
        discussionDatasource
            .getDiscussions(query = discussionCriteria.toQuery(), cursor = cursor, size = size)
            .mapCatching { it.toDomain() }  // DTO → Domain 변환, 예외도 Result로 감쌈
}
```

### 매핑 확장 함수

```kotlin
// Domain → UiModel
fun DiscussionCatalog.toUiModel(): DiscussionUiModel = when (this) {
    is OnlineDiscussionCatalog -> DiscussionUiModel.Online(...)
    is OfflineDiscussionCatalog -> DiscussionUiModel.Offline(...)
}

// State → Domain
private fun CreateDiscussionState.Online.toDomain(): OnlineDiscussionDraft =
    OnlineDiscussionDraft(
        title = title.trim(),
        content = content.trim(),
        endDate = today.plus(DatePeriod(days = selectedEndDateIndex.toEndDateOffsetDays())),
    )
```

### Filter Debounce 패턴

```kotlin
private val filterChanged = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)

init {
    filterChanged
        .debounce(FILTER_DEBOUNCE_MILLIS)
        .onEach { refreshListInternal() }
        .launchIn(viewModelScope)
}
```

### Sealed Interface — 상태 변형이 여러 종류일 때

```kotlin
sealed interface CreateDiscussionState : UiState {
    data class Online(val title: String = "", ...) : CreateDiscussionState
    data class Offline(val place: String = "", ...) : CreateDiscussionState
}

// ViewModel에서 스마트 캐스트 사용
when (val state = currentState) {
    is CreateDiscussionState.Online -> updateState { state.copy(title = newTitle) }
    is CreateDiscussionState.Offline -> updateState { state.copy(place = newPlace) }
}
```

### Koin DI 모듈

```kotlin
// feature/xxx/impl/di/XxxModule.kt
val discussionListModule = module {
    viewModel {
        DiscussionListViewModel(
            discussionRepository = get(),
            authRepository = get(),
        )
    }
    single { DiscussionListNavKeyProvider() } bind NavKeyProvider::class
}

// composeApp/di/ 에서 조립
val featureModule = module {
    includes(discussionListModule, discussionDetailModule, ...)
}
```

---

## 4. 빌드/테스트 명령어

```bash
# 코드 포맷 (커밋 전 필수)
./gradlew ktlintFormat

# 코드 스타일 검사
./gradlew ktlintCheck

# 디버그 APK 빌드
./gradlew composeApp:assembleDebug

# 릴리즈 APK 빌드
./gradlew composeApp:assembleRelease

# 전체 빌드
./gradlew build

# 클린 빌드
./gradlew clean build

# 특정 모듈만 빌드
./gradlew :feature:discussionlist:impl:build

# 테스트 실행
./gradlew test
```

### 주요 버전

| 항목 | 버전 |
|------|------|
| Kotlin | 2.2.21 |
| Compose Multiplatform | 1.10.0 |
| compileSdk | 36 |
| minSdk | 29 |
| Koin | 4.1.1 |
| Ktor | 3.3.3 |

---

## 5. 새 기능 추가 시 따라야 할 구조

### 체크리스트

```
feature/newfeature/
├── api/
│   └── src/commonMain/.../api/
│       └── NewFeatureNavKey.kt          ✅ data object 또는 data class
└── impl/
    └── src/commonMain/.../impl/
        ├── NewFeatureScreen.kt          ✅ koinViewModel() 사용
        ├── viewmodel/
        │   ├── NewFeatureViewModel.kt   ✅ BaseViewModel 상속
        │   ├── NewFeatureState.kt       ✅ @Immutable data class : UiState
        │   ├── NewFeatureIntent.kt      ✅ sealed interface : UiIntent
        │   └── NewFeatureEffect.kt      ✅ sealed interface : UiEffect
        ├── model/
        │   └── NewFeatureUiModel.kt     ✅ UI 전용 모델 (domain 모델 직접 노출 금지)
        ├── component/
        │   └── NewFeatureXxxSection.kt  ✅ 재사용 컴포넌트 분리
        ├── navigation/
        │   ├── Navigation.kt            ✅ composable { } route 등록
        │   └── NewFeatureNavKeyProvider.kt
        └── di/
            └── NewFeatureModule.kt      ✅ viewModel { } + NavKeyProvider bind
```

### 신규 feature 추가 시 필수 작업

1. `feature/newfeature/api`, `feature/newfeature/impl` 디렉토리 및 `build.gradle.kts` 생성
2. `impl/build.gradle.kts`에 `id("dialog.convention.kotlin.feature.impl")` 플러그인 적용
3. `impl/di/NewFeatureModule.kt` 작성 후 `composeApp/di/` 의 `featureModule`에 `includes()` 추가
4. `navigation/Navigation.kt`에 route 등록 후 `feature/main` 또는 관련 네비게이션 그래프에 연결
5. NavKey를 사용하는 feature의 `build.gradle.kts`에 `api` 모듈 의존성 추가

### 신규 API 엔드포인트 추가 시

1. `core/network` — Datasource 인터페이스 메서드 추가 + RemoteDatasource 구현
2. `core/network` — Request/Response DTO 추가 (`@Serializable`)
3. `core/domain` — Repository 인터페이스 메서드 추가
4. `core/data` — DefaultRepository 구현 (`.mapCatching { it.toDomain() }`)
5. `core/model` — 필요 시 도메인 모델 추가

---

## 6. 주요 의존성

| 라이브러리 | 용도 |
|-----------|------|
| Koin 4.1.1 | 의존성 주입, `koinViewModel()` |
| Ktor 3.3.3 | HTTP 클라이언트 |
| Compose Multiplatform 1.10.0 | UI |
| kotlinx-collections-immutable | `ImmutableList`, `persistentListOf()` |
| kotlinx-coroutines 1.10.2 | Flow, suspend |
| kotlinx-datetime 0.7.1 | 날짜/시간 |
| Coil 3.3.0 | 이미지 로딩 |
| Room 2.8.4 | 로컬 DB |
| KtLint 14.0.1 | 코드 포맷 |
