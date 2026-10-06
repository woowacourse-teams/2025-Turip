# 인기 관광지 → 지역 브리핑 화면 (계획)

> 2026-09-10 작성. `docs/popular-region.md` 의 후속 작업 계획.
> 아직 코드는 손대지 않은 상태다.

## 1. 무엇을 바꾸나

인기 관광지 지도의 바텀시트(`PopularRegionSheetContent`)에서 **`연관 콘텐츠 보기`** 를 누르면
지금은 검색 결과 화면(`RegionResultNavKey`)으로 간다.
이걸 **랜덤 여행 브리핑과 같은 모양의 지역 브리핑 화면**으로 바꾼다.

브리핑 화면은 랜덤 여행 것을 그대로 쓰지 않고, 뽑기 맥락에 묶인 것들을 덜어낸다.

| 랜덤 여행 브리핑 | 지역 브리핑 |
|---|---|
| 상단 티켓(`PrintedTicket`) | **제거** |
| 앱바 제목 `랜덤 여행` | **지역명** (`강릉`) |
| — | 앱바 아래 **방문자 수 카드** (`2025년 6월 방문자 수 · 480만`) |
| 영상 목록 (탭 → 선택 하이라이트) | 영상 목록 (**탭 → 영상 상세로 바로 이동**) |
| 연관 관광지 목록 | 그대로 |
| 하단 CTA `다시 돌리기` / `이 여행 시작하기` | **제거** (하단 바 통째로 없음) |
| 튜립 생성 시트(`TuripDraftBottomSheet`) | **제거** |

## 2. 결정 사항

| 질문 | 답 |
|---|---|
| 영상 카드 탭 | 선택 상태 없이 곧장 `TripDetailNavKey` 로 이동 |
| 지역명 / 방문자 수 위치 | 지역명은 앱바 중앙, 방문자 수는 본문 최상단 카드(스크롤됨) |
| 방문자 수 출처 | **NavKey 파라미터로 전달**. 새 화면에서 재조회하지 않는다 |
| 화면 위치 | **새 모듈 `feature/regionbriefing`** (api + impl) |

## 3. 새 모듈 vs 재사용 — 정리

`randomtravel:impl` 안에 넣으면 브리핑 UI를 곧장 공유할 수 있지만,
지역 브리핑은 뽑기와 아무 관계가 없어서 모듈 이름과 내용이 어긋난다.
**새 모듈 `feature/regionbriefing`** 로 간다. 대신 `feature impl 끼리 직접 의존 금지` 규칙 때문에
공유하려는 UI를 어디에 둘지가 유일한 쟁점이 된다.

### 공유 대상

브리핑 본문에서 두 화면이 똑같이 쓰는 것:

- `RandomTravelVideoItem` (영상 카드, 약 130줄)
- `RandomTravelRelatedSpotItem` (연관 관광지 카드)
- `RandomTravelVideoModel`, `RandomTravelRelatedSpotModel` + `VideoInformation.toUiModel()`, `RelatedSpotCategory.toUiModel()`
- 섹션 헤더(`BriefingSectionHeader`) / 안내 자리(`BriefingNotice`, `BriefingPlaceholder`)

### 방안 A — `core:ui` 로 올려서 공유 (**권장**)

`core:ui` 는 이미 `component/ErrorScreen.kt`, `component/NameEditorSheetContent.kt`,
`model/turip/*`, `util/TuripUrlConverter` 를 갖고 있어 선례가 있다. `core:model` 의존도 이미 있어
매퍼까지 그대로 옮겨 갈 수 있다.

```
core/ui/component/VideoSummaryItem.kt      ← RandomTravelVideoItem (isSelected 기본값 false)
core/ui/component/RelatedSpotItem.kt       ← RandomTravelRelatedSpotItem
core/ui/component/BriefingSectionHeader.kt ← 헤더 + Notice/Placeholder
core/ui/model/content/VideoSummaryModel.kt ← RandomTravelVideoModel + 매퍼
core/ui/model/region/RelatedSpotModel.kt   ← RandomTravelRelatedSpotModel + 매퍼
```

- 장점: 중복 0. 카드 디자인이 바뀌면 두 화면이 같이 바뀐다.
- 단점: `randomtravel:impl` 을 함께 고쳐야 한다 (`RandomTravelState` 의 `videos`/`relatedSpotsUiState` 타입,
  `BriefingSection` import). 랜덤 여행 회귀 위험이 조금 있고 diff 가 커진다.
- 문자열 리소스는 `core:designsystem` 에 모여 있어 모듈 이동과 무관하다.
  다만 `random_travel_briefing_video_title` 같은 접두사는 그대로 두고
  지역 브리핑 전용 문구만 `region_briefing_*` 로 새로 추가한다.

### 방안 B — 새 모듈에 복제

`randomtravel:impl` 을 전혀 건드리지 않는다. 대신 카드 UI 약 300줄이 두 벌이 된다.
급하게 화면부터 띄워야 할 때만 고른다.

> **권장: A.** 새 모듈로 가르는 이유가 `경계를 깨끗하게` 인데, 공유 UI를 복제하면 그 이득이 반쯤 사라진다.
> A 로 진행하되 `randomtravel` 회귀는 브리핑 화면 수동 확인으로 막는다.

## 4. 모듈 구성

```
feature/regionbriefing/
├── api/
│   └── RegionBriefingNavKey.kt
└── impl/
    ├── RegionBriefingScreen.kt
    ├── RegionBriefingViewModel.kt
    ├── RegionBriefingState.kt
    ├── RegionBriefingIntent.kt
    ├── RegionBriefingEffect.kt
    ├── component/RegionVisitorCard.kt
    ├── navigation/RegionBriefingNavKeyProvider.kt
    └── di/RegionBriefingModule.kt
```

### NavKey

```kotlin
@Serializable
data class RegionBriefingNavKey(
    val regionCategoryName: String,   // "강릉" — 콘텐츠/연관 관광지 조회 키
    val visitorCount: Long,           // 시트가 이미 갖고 있는 값
    val baseMonth: String?,           // "202506". 서버에 데이터 없으면 null
) : NavKey
```

방문자 수를 다시 조회하지 않으므로 **시트에 보이던 숫자와 브리핑 숫자가 항상 같다.**
`visitorCountText`(`480만`) 포맷과 `baseMonthText`(`2025년 6월`) 포맷 함수는 지금 각각
`popularregion:impl` 의 `PopularRegionModel`, `component/BaseMonthText.kt` 안에 있다.
둘 다 짧으니 **`core:ui/util` 로 옮겨** 두 모듈이 같이 쓴다.

### 의존성

`feature/regionbriefing/impl/build.gradle.kts`

```kotlin
implementation(project(":feature:regionbriefing:api"))
implementation(project(":feature:randomtravel:api"))  // RelatedSpotDetailNavKey
implementation(project(":feature:trip:api"))          // TripDetailNavKey
implementation(project(":feature:login:api"))
implementation(project(":core:data"))
```

`popularregion:impl` 은 `:feature:search:api` → `:feature:regionbriefing:api` 로 교체한다.
`RegionResultNavKey` 는 이 화면에서만 빠지는 것이고 홈/검색은 그대로 쓴다.

연관 관광지 카드를 눌렀을 때 가는 `RelatedSpotDetailNavKey` 는 `randomtravel:api` 에 있다.
api 모듈 참조라 규칙 위반이 아니다. 화면을 옮기지 않고 그대로 재사용한다.

## 5. 상태 설계

`RandomTravelState` 에서 뽑기·튜립 관련을 걷어낸 모양이다.

```kotlin
@Immutable
data class RegionBriefingState(
    val regionName: String = "",
    val visitorCountText: String = "",
    val baseMonthText: String = "",
    val videos: ImmutableList<VideoSummaryModel> = persistentListOf(),
    val relatedSpotsUiState: RelatedSpotsUiState = RelatedSpotsUiState.Loading,
    val isLoading: Boolean = false,
    val isFetched: Boolean = false,
    val isVideoListLoadable: Boolean = false,
    val isLoadingMoreVideos: Boolean = false,
    val errorUiState: ErrorUiState = ErrorUiState.None,
) : UiState {
    val shouldShowEmptyVideos: Boolean =
        isFetched && videos.isEmpty() && errorUiState == ErrorUiState.None
}
```

`RelatedSpotsUiState`(Loading / Success / Unsupported / Error) 는 `randomtravel:impl` 에 있는 것과
같은 정의라 **`core:ui` 로 함께 올린다**(방안 A 범위).

```kotlin
sealed interface RegionBriefingIntent : UiIntent {
    data class ClickVideo(val contentId: Long) : RegionBriefingIntent
    data class ClickRelatedSpot(val spotCategory: String) : RegionBriefingIntent
    data object LoadMoreVideos : RegionBriefingIntent
    data object Retry : RegionBriefingIntent
    data object RetryRelatedSpots : RegionBriefingIntent
}

sealed interface RegionBriefingEffect : UiEffect {
    data class NavigateToTripDetail(val contentId: Long) : RegionBriefingEffect
    data class NavigateToRelatedSpotDetail(
        val regionCategoryName: String,
        val spotCategory: String,
    ) : RegionBriefingEffect
    data object NavigateToLogin : RegionBriefingEffect
}
```

ViewModel 은 `RandomTravelViewModel` 의 `loadBriefing` / `loadMoreVideos` / `loadRelatedSpots` /
`handleBriefingError` 만 옮겨 오면 된다 (약 120줄). 추첨·튜립 초안 로직(약 250줄)은 따라오지 않는다.
`ContentRepository`, `RegionRepository`, `SessionManager` 세 개만 주입한다.
NavKey 값은 Koin `parametersOf` 로 넘긴다 (기존 `RelatedSpotDetailViewModel` 과 같은 방식이면 그대로 따른다 —
착수 시 `RelatedSpotDetailScreen` 이 인자를 어떻게 받는지 먼저 확인할 것).

## 6. 화면 구성

```
┌────────────────────────────────┐
│  ←        강릉                  │  TuripAppBar (center = 지역명)
├────────────────────────────────┤
│ ┌────────────────────────────┐ │
│ │ 2025년 6월 방문자 수         │ │  RegionVisitorCard
│ │ 480만                       │ │  (시트의 RegionStatCard 와 같은 톤,
│ └────────────────────────────┘ │   `연관 콘텐츠 보기` 버튼만 없음)
│                                │
│ 🎥 강릉 여행 영상        [12개] │  BriefingSectionHeader
│ ┌────────────────────────────┐ │
│ │ 영상 카드                   │ │  탭 → TripDetail
│ └────────────────────────────┘ │
│           더보기                │  3개까지 미리보기 → 펼침 → 다음 페이지
│                                │
│ 🎉 강릉 연관 관광지             │
│ ┌────────────────────────────┐ │
│ │ 관광지 · 숙박 · 음식        │ │  탭 → RelatedSpotDetail
│ └────────────────────────────┘ │
└────────────────────────────────┘
```

- 하단 고정 바가 없으므로 `LazyColumn` 하나가 화면을 다 쓴다.
  `RandomTravelScreen` 의 `Column { LazyColumn(weight(1f)); CtaSection }` 구조에서 CTA 를 뺀 모양.
- 영상 `더보기` 동작(먼저 로컬 3개 펼치고 → 다 펼치면 다음 페이지 요청)은 그대로 가져온다.
- 영상 0건 지역은 기존 `random_travel_empty_video` 문구를 그대로 쓴다.

## 7. 시트 쪽 변경

`PopularRegionEffect`

```kotlin
data class NavigateToRegionBriefing(
    val regionCategoryName: String,
    val visitorCount: Long,
    val baseMonth: String?,
) : PopularRegionEffect
```

- `NavigateToRegionResult` 는 삭제한다.
- `PopularRegionViewModel.ClickRelatedContents` 처리에서 선택된 지역의 `visitorCount` 와
  `state.baseMonth` 를 함께 싣는다.
- `PopularRegionScreen` 의 `onRegionContentsClick: (String) -> Unit` 콜백 시그니처가
  `(String, Long, String?) -> Unit` 으로 늘어난다. `PopularRegionNavKeyProvider` 에서 `RegionBriefingNavKey` 로 이동.
- 시트 버튼 문구(`연관 콘텐츠 보기`)와 노출 조건(`hasRegionCategory` 인 지역만)은 그대로 둔다.
  시도 층(강원 등)은 여전히 버튼이 없다.
- 뒤로가기로 지도에 돌아오면 `PopularRegionViewModel` 이 살아 있어 선택 상태가 남고
  시트가 다시 열린다. 지금 동작 그대로다.

## 8. 작업 순서

1. **공유 UI 승격** — `core:ui` 로 카드/모델/매퍼/`RelatedSpotsUiState` 이동,
   `randomtravel:impl` 을 새 경로로 맞추고 브리핑 화면이 그대로인지 확인
2. **모듈 생성** — `settings.gradle.kts` 에 `:feature:regionbriefing:api`, `:impl` 추가,
   `build.gradle.kts` 2개(`turip.convention.kotlin.feature.impl`)
3. **NavKey** — `RegionBriefingNavKey`
4. **MVIA** — State / Intent / Effect / ViewModel
5. **화면** — `RegionBriefingScreen` + `RegionVisitorCard`, 문자열 `region_briefing_*` 추가
6. **배선** — `RegionBriefingNavKeyProvider`, `regionBriefingModule`,
   `composeApp/di/Modules.kt` 의 `featureModule` 에 `includes()`
7. **시트 연결** — `PopularRegionEffect` / ViewModel / Screen / NavKeyProvider 수정,
   `popularregion:impl` 의존성 `search:api` → `regionbriefing:api`
8. `./gradlew ktlintFormat` → `./gradlew composeApp:assembleDebug`

## 9. 진행 결과 (2026-09-10 구현 완료)

계획대로 **방안 A + 새 모듈**로 넣었다. `./gradlew composeApp:assembleDebug` 및
`:feature:regionbriefing:impl:compileKotlinIosSimulatorArm64` 통과.

계획과 달라진 것:

- **문자열 키를 함께 정리했다.** 공유 컴포넌트가 `random_travel_*` 리소스를 참조하는 게 어색해
  브리핑 공용 문구 10개를 `briefing_*` 로 바꿨다 (`briefing_video_title`, `briefing_related_spot_count` 등).
  `random_travel_empty_video` 는 `다시 돌려볼까요?` 가 붙어 있어 뽑기 전용으로 남기고,
  지역 브리핑용 `region_briefing_empty_video` 를 따로 추가했다.
- **`PopularRegionState` 의 기준월 파싱도 함께 올렸다.** `core:ui/util/BaseMonth.kt` 가
  `toBaseYear()` / `toBaseMonthOfYear()` 와 `baseMonthText` 두 오버로드를 갖는다.
  지도는 연/월을 따로 갖고 있고 브리핑은 `yyyyMM` 을 그대로 받아서다.
- **영상 개수 칩**은 랜덤 여행이 `destination.videoCount`(서버가 준 전체 수)를 쓰지만
  지역 브리핑은 그 값을 받을 데가 없어 **받아온 개수**(`videos.size`)를 보여준다.
  더보기로 다음 페이지를 붙이면 숫자가 늘어난다.
- `ktlintFormat` 전체 실행은 `core:data/mapper/toPostRequestDto.kt` 의 기존 파일명 위반
  (`standard:filename`)에서 멈춘다. 이번 작업과 무관해 손대지 않았고, 건드린 모듈만 따로 포맷했다.

새로 생긴 파일:

```
core/ui/component/{VideoSummaryItem, RelatedSpotItem, BriefingSection}.kt
core/ui/model/content/VideoSummaryModel.kt
core/ui/model/region/{RelatedSpotModel, RelatedSpotsUiState}.kt
core/ui/util/{BaseMonth, VisitorCount}.kt
feature/regionbriefing/api/…/RegionBriefingNavKey.kt
feature/regionbriefing/impl/…/{RegionBriefingScreen, ViewModel, State, Intent, Effect}.kt
feature/regionbriefing/impl/…/component/RegionVisitorCard.kt
feature/regionbriefing/impl/…/navigation/RegionBriefingNavKeyProvider.kt
feature/regionbriefing/impl/…/di/RegionBriefingModule.kt
```

지워진 파일: `randomtravel:impl` 의 `RandomTravelVideoItem` · `RandomTravelRelatedSpotItem` ·
`RandomTravelVideoModel` · `RandomTravelRelatedSpotModel`, `popularregion:impl` 의 `BaseMonthText.kt`.

## 10. 남은 확인거리

- **앱바 제목이 긴 지역명**(`서귀포시` 등)에서 넘치지 않는지. 현재 후보 14곳은 모두 2~3자라 문제없어 보인다.
- **방문자 수만 있고 콘텐츠가 없는 지역**은 애초에 버튼이 안 뜨므로 이 화면에 도달하지 않는다.
  다만 `regionCategory` 가 있는데 영상이 0건인 경우는 도달 가능 → 빈 상태 문구로 처리.
- **연관 관광지 미지원 지역**(14곳 중 공주/여수/수원/군산 등)은 `Unsupported` 안내가 그대로 보인다.
  섹션을 숨기지 않는 기존 판단을 유지한다.
- `RegionResultNavKey` 로 가던 경로가 사라지면서 인기 지역 → 검색 결과 유입이 끊긴다.
  지표를 보고 있었다면 미리 알릴 것.
