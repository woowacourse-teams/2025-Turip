---
topic: trip
last_compiled: 2026-04-13
source_count: 18
status: active
---

# Trip (콘텐츠 상세 & 장소 탐색)

## 목적 [coverage: high — 18 sources]

여행 콘텐츠(비디오 포함)의 상세 정보를 WebView와 네이티브 UI를 혼합하여 표시하고, 콘텐츠에 포함된 장소를 튜립에 추가하는 흐름을 담당한다. `TripDetailScreen`은 크리에이터 정보, 콘텐츠 영상, 장소 목록, 북마크 버튼, 지도를 포함한다. `TuripSelectionScreen`은 특정 장소를 어느 튜립에 추가/제거할지 선택하는 화면이다.

## 아키텍처 [coverage: high — 18 sources]

```
TripDetailScreen
  ├─ TripDetailAppBar          → 뒤로가기, 공유
  ├─ ContentInformation        → 콘텐츠 제목, 설명
  ├─ CreatorInformation        → 크리에이터 프로필, 이름
  ├─ ContentVideo              → WebView 기반 영상 재생
  ├─ ContentExpandableTitle    → 접기/펼치기 섹션
  ├─ PlaceItem                 → 장소 카드 (지도 핀, 이름, 카테고리)
  └─ ContentBookmarkButton     → 북마크 토글

TripDetailViewModel (HiltViewModel)
  ├─ uiState: StateFlow<TripDetailUiState>
  ├─ loadContent(contentId)    → GET /v1/contents/{id}
  ├─ toggleBookmark()          → POST/DELETE /v1/bookmarks
  └─ TripDetailWebViewController → WebView 상태 관리

TuripSelectionScreen (장소 → 튜립 선택)
  ├─ 내 튜립 목록 (드래그 정렬 포함)
  ├─ 체크박스로 장소 포함 여부 선택
  └─ 확인 시 PUT /v1/turips/places/{placeId} 일괄 연결

WebView 구성:
  ├─ TuripWebViewClient        → 페이지 로드 콜백
  ├─ TuripWebChromeClient      → fullscreen 영상 지원
  ├─ WebViewVideoBridge        → JS ↔ Kotlin 영상 제어 인터페이스
  └─ VideoManager              → ExoPlayer 또는 WebView 영상 라이프사이클 관리
```

## 의존 관계 [coverage: high — 18 sources]

```
TripDetailViewModel
  ├─ ContentRepository (domain)
  │    └─ DefaultContentRepository (data)
  │         └─ ContentService (Ktorfit, GET /v1/contents/{id})
  ├─ BookmarkRepository (domain)
  │    └─ DefaultBookmarkRepository (data)
  │         └─ BookmarkService (Ktorfit)
  └─ CreatorRepository (domain)
       └─ DefaultCreatorRepository (data)

TuripSelectionViewModel
  └─ TuripRepository → getTurips, putPlaceTurips, getTuripsByPlaceId
```

## API 표면 [coverage: high — 18 sources]

### `ContentService` (Ktorfit, 상세 관련)

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| `getContent(contentId)` | `GET /v1/contents/{contentId}` | 콘텐츠 상세 |

### `BookmarkService`

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| `postBookmark(contentId)` | `POST /v1/bookmarks` | 북마크 추가 |
| `deleteBookmark(contentId)` | `DELETE /v1/bookmarks/{contentId}` | 북마크 제거 |
| `getBookmarks(size, lastId)` | `GET /v2/bookmarks` | 북마크 목록 (v2, 페이징) |

### `TripDetailNavKey`

```kotlin
@Serializable
data class TripDetailNavKey(val contentId: Long) : NavKey
```

## 데이터 [coverage: high — 18 sources]

### `TripDetailUiState`

```kotlin
data class TripDetailUiState(
    val isLoading: Boolean,
    val content: Content?,
    val isBookmarked: Boolean,
    val errorUiState: ErrorUiState,
)
```

### 도메인 모델

| 클래스 | 주요 필드 |
|--------|-----------|
| `Content` | `id`, `creator: Creator`, `videoData: VideoData`, `places: List<Place>`, `isBookmarked` |
| `Place` | `id`, `name`, `category`, `latitude`, `longitude` |
| `PlaceModel` (UI) | `id`, `name`, `category`, `latLng: PlaceLatLngUiModel` |
| `MapModel` (UI) | 지도에 표시할 좌표 목록 |

### `TuripSelectionScreen` 상태

사용자가 장소를 포함할 튜립을 선택하면 `isSelected` 플래그가 토글된다. 확인 시 변경된 선택 상태 전체를 `PUT /v1/turips/places/{placeId}`로 일괄 전송한다.

드래그 정렬은 `turipselection/util/reorderable/` 패키지의 커스텀 `ReorderableState`로 구현되어 있다.

## 주요 결정사항 [coverage: high — 18 sources]

1. **WebView 혼합 UI**: 영상 콘텐츠는 WebView로, 나머지 메타데이터(제목, 장소, 크리에이터)는 네이티브 Compose UI로 렌더링한다. `TuripWebChromeClient`가 fullscreen 영상 전환을 처리한다.
2. **일괄 튜립 연결**: 장소를 여러 튜립에 동시에 추가/제거할 때 개별 API를 반복 호출하지 않고 `PUT /v1/turips/places/{placeId}` 단일 요청으로 처리한다.
3. **드래그 정렬**: `TuripSelectionScreen`에서 튜립 목록을 드래그로 정렬할 수 있다. 순서 변경 결과는 `PATCH /v1/turips/places/turip-order`로 저장된다.

## 주의사항 [coverage: medium — 8 sources]

- `VideoManager`는 WebView와 ExoPlayer 라이프사이클을 모두 관리한다. `TripDetailScreen`이 Compose 트리에서 벗어날 때(navigate out) `VideoManager.release()`를 명시적으로 호출해야 한다.
- `ContentBookmarkButton`은 낙관적 업데이트(optimistic update) 방식을 사용한다. 서버 요청 실패 시 이전 상태로 되돌린다.

## 출처 [coverage: high — 18 sources]

- [TripDetailScreen.kt](../../app/src/main/java/com/on/turip/ui/compose/trip/TripDetailScreen.kt)
- [TripDetailUiState.kt](../../app/src/main/java/com/on/turip/ui/compose/trip/TripDetailUiState.kt)
- [TripDetailWebViewController.kt](../../app/src/main/java/com/on/turip/ui/compose/trip/TripDetailWebViewController.kt)
- [VideoManager.kt](../../app/src/main/java/com/on/turip/ui/compose/trip/webview/VideoManager.kt)
- [TuripWebChromeClient.kt](../../app/src/main/java/com/on/turip/ui/compose/trip/webview/TuripWebChromeClient.kt)
- [TuripWebViewClient.kt](../../app/src/main/java/com/on/turip/ui/compose/trip/webview/TuripWebViewClient.kt)
- [WebViewVideoBridge.kt](../../app/src/main/java/com/on/turip/ui/compose/trip/webview/WebViewVideoBridge.kt)
- [ContentInformation.kt](../../app/src/main/java/com/on/turip/ui/compose/trip/component/ContentInformation.kt)
- [PlaceItem.kt](../../app/src/main/java/com/on/turip/ui/compose/trip/component/PlaceItem.kt)
- [ContentBookmarkButton.kt](../../app/src/main/java/com/on/turip/ui/compose/trip/component/ContentBookmarkButton.kt)
- [ContentVideo.kt](../../app/src/main/java/com/on/turip/ui/compose/trip/component/ContentVideo.kt)
- [TripDetailNavKey.kt](../../app/src/main/java/com/on/turip/ui/compose/trip/navigation/TripDetailNavKey.kt)
- [PlaceModel.kt](../../app/src/main/java/com/on/turip/ui/compose/trip/model/PlaceModel.kt)
- [MapModel.kt](../../app/src/main/java/com/on/turip/ui/compose/trip/model/MapModel.kt)
- [TuripDetailNavKey.kt](../../app/src/main/java/com/on/turip/ui/compose/turipdetail/navigation/TuripDetailNavKey.kt)
- [ContentRepository.kt](../../app/src/main/java/com/on/turip/domain/content/repository/ContentRepository.kt)
- [BookmarkRepository.kt](../../app/src/main/java/com/on/turip/domain/bookmark/repository/BookmarkRepository.kt)
- [TuripRepository.kt](../../app/src/main/java/com/on/turip/domain/turip/repository/TuripRepository.kt)
