---
topic: design-system
last_compiled: 2026-04-13
source_count: 12
status: active
---

# Design System

## 목적 [coverage: high — 12 sources]

Turip 앱의 디자인 토큰(색상, 타이포그래피, 간격, 형태)과 공용 UI 컴포넌트를 제공한다. `TuripTheme`이 `MaterialTheme`을 확장하여 앱 전체에 일관된 디자인을 적용한다. 스낵바는 `LocalSnackbarDelegate`로 전역에서 접근 가능하다.

## 아키텍처 [coverage: high — 12 sources]

```
TuripTheme (MaterialTheme 래퍼)
  ├─ TuripColor (CompositionLocal)      → TuripColors 인스턴스
  ├─ MaterialTheme.colorScheme          → Material3 ColorScheme (TuripColor 기반)
  ├─ MaterialTheme.typography           → TuripTypography (Pretendard)
  ├─ MaterialTheme.shapes               → TuripShapes
  └─ LocalSnackbarDelegate              → 전역 스낵바 접근

공용 컴포넌트 (ui/compose/designsystem/component/)
  ├─ TuripAppBar       → 3슬롯 앱바 (start / center / end)
  ├─ TuripDialog       → Card 기반 2버튼 다이얼로그
  ├─ TuripSnackbar     → 아이콘 포함 스낵바
  └─ ErrorScreen       → 에러 상태 화면 (재시도 버튼)

공용 컴포넌트 (ui/common/)
  ├─ component/bookmark/  → 북마크 버튼, 아이콘
  ├─ component/content/   → 콘텐츠 카드, 썸네일
  ├─ error/               → ErrorUiState 처리 컴포넌트
  ├─ extensions/          → Modifier, Composable 확장 함수
  ├─ mapper/              → UI 모델 매퍼
  ├─ model/               → 공유 UI 모델 (trip, turip, namestatus)
  └─ paging/              → PagingState<T> 처리 유틸
```

## 의존 관계 [coverage: medium — 5 sources]

```
TuripTheme
  └─ MaterialTheme (Compose Material3)

LocalSnackbarDelegate (CompositionLocal)
  └─ SnackbarDelegate (인터페이스)
       └─ 각 화면의 LaunchedEffect에서 uiEffect 수집 후 showSnackbar() 호출

ErrorScreen
  └─ ErrorUiState (None / Network / Server / Unexpected)
```

## API 표면 [coverage: high — 12 sources]

### `TuripTheme`

```kotlin
@Composable
fun TuripTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
)
```

현재 라이트 모드만 지원한다 (`darkTheme` 파라미터는 예약됨).

### `TuripAppBar`

```kotlin
@Composable
fun TuripAppBar(
    startContent: (@Composable () -> Unit)? = null,
    centerContent: (@Composable () -> Unit)? = null,
    endContent: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
)
```

### `TuripDialog`

```kotlin
@Composable
fun TuripDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
)
```

### `TuripSnackbar` + `TuripSnackbarVisuals`

```kotlin
data class TuripSnackbarVisuals(
    override val message: String,
    val icon: SnackbarIconModel? = null,  // Vector 또는 Painter
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
) : SnackbarVisuals
```

`LocalSnackbarDelegate.current.showSnackbar(visuals)`로 전역 호출한다.

### 디자인 토큰 접근

```kotlin
// 색상
MaterialTheme.colorScheme.primary   // Material3 표준
LocalTuripColors.current.gray100    // Turip 커스텀 색상

// 간격
TuripTheme.spacing.medium           // 16dp

// 형태
TuripTheme.shapes.container         // 8dp rounded
```

## 데이터 [coverage: high — 12 sources]

### 색상 팔레트 (`TuripColor`)

| 토큰 | 값 | 용도 |
|------|-----|------|
| `Blue` | `#5AC3D5` | Primary |
| `LightBlue` | 연한 파랑 | Secondary |
| `Red` | 에러 색상 | Error |
| `LightBeige` | 칩 배경 | Chip background |
| `Gray100~500` | 5단계 회색 | 텍스트, 배경, 구분선 |

### 간격 토큰 (`TuripSpacing`) — 9개

`extra_small(4dp)` ~ `extra_large(32dp)` 범위.

### 형태 토큰 (`TuripShape`) — 5개

| 토큰 | 값 |
|------|-----|
| `container` | 8dp rounded |
| `largeContainer` | 16dp rounded |
| `chip` | 12dp rounded |
| `bottomSheetRounded` | top 16dp rounded |
| `wideButton` | 24dp rounded |

### 타이포그래피 (`TuripTypography`) — Pretendard 폰트

`display(26sp Bold)` ~ `info2(10sp Normal)` 8개 스타일.

### `PagingState<T>` (ui/common/paging)

```kotlin
sealed interface PagingState<out T> {
    data object Loading : PagingState<Nothing>
    data object Empty : PagingState<Nothing>
    data class Success<T>(val data: ImmutableList<T>, val hasMore: Boolean) : PagingState<T>()
    data class Error(val errorUiState: ErrorUiState) : PagingState<Nothing>()
}
```

## 주요 결정사항 [coverage: high — 12 sources]

1. **Material3 확장**: `MaterialTheme`을 직접 대체하지 않고 래핑하여 Material3 컴포넌트와 호환성을 유지한다.
2. **`LocalSnackbarDelegate`**: 스낵바를 피처 화면에서 직접 `SnackbarHostState`에 접근하지 않고 `CompositionLocal`을 통해 호출한다. ViewModel `uiEffect`와의 연결이 각 화면의 `LaunchedEffect`에서 이루어진다.
3. **`ImmutableList` 사용**: 공유 UI 모델과 상태에 `kotlinx.collections.immutable.ImmutableList`를 사용하여 Compose recomposition 최적화(`@Stable` 추론)에 기여한다.
4. **`ErrorUiState` 통일**: 모든 화면의 에러 상태가 `ErrorUiState(None/Network/Server/Unexpected)` 단일 타입으로 표현된다. `ErrorScreen` 컴포넌트가 재시도 버튼을 포함하여 일관된 에러 UX를 제공한다.

## 주의사항 [coverage: medium — 5 sources]

- `LocalSnackbarDelegate`는 `TuripTheme` 내부에서 제공된다. `TuripTheme` 외부에서 접근하면 `LocalSnackbarDelegate.current`가 기본 `NoOpSnackbarDelegate`를 반환하여 스낵바가 표시되지 않는다.
- 다크 모드는 현재 지원하지 않는다. `isSystemInDarkTheme()`이 `true`인 환경에서도 라이트 색상이 사용된다.

## 출처 [coverage: high — 12 sources]

- [Theme.kt](../../app/src/main/java/com/on/turip/ui/compose/designsystem/theme/Theme.kt)
- [TuripColor.kt](../../app/src/main/java/com/on/turip/ui/compose/designsystem/theme/TuripColor.kt)
- [TuripSpacing.kt](../../app/src/main/java/com/on/turip/ui/compose/designsystem/theme/TuripSpacing.kt)
- [TuripShape.kt](../../app/src/main/java/com/on/turip/ui/compose/designsystem/theme/TuripShape.kt)
- [Type.kt](../../app/src/main/java/com/on/turip/ui/compose/designsystem/theme/Type.kt)
- [TuripAppBar.kt](../../app/src/main/java/com/on/turip/ui/compose/designsystem/component/TuripAppBar.kt)
- [TuripDialog.kt](../../app/src/main/java/com/on/turip/ui/compose/designsystem/component/TuripDialog.kt)
- [TuripSnackbar.kt](../../app/src/main/java/com/on/turip/ui/compose/designsystem/component/TuripSnackbar.kt)
- [ErrorScreen.kt](../../app/src/main/java/com/on/turip/ui/compose/designsystem/component/ErrorScreen.kt)
- [LocalSnackbarDelegate.kt](../../app/src/main/java/com/on/turip/ui/compose/designsystem/snackbar/LocalSnackbarDelegate.kt)
- [SnackbarIconModel.kt](../../app/src/main/java/com/on/turip/ui/compose/designsystem/model/SnackbarIconModel.kt)
- [PagingState.kt (ui/common/paging)](../../app/src/main/java/com/on/turip/ui/common/paging/)
