---
topic: turip
last_compiled: 2026-04-13
source_count: 22
status: active
---

# Turip

## 목적 [coverage: high — 22 sources]

앱의 핵심 기능. "튜립(Turip)"은 사용자가 여러 장소를 묶어 관리하는 여행 폴더다. 사용자는 튜립을 생성·수정·삭제하고, 장소를 추가/제거/순서 변경할 수 있다. 튜립은 공유 가능하며, 초대 링크를 통해 다른 사용자가 참여할 수 있다. 실시간 협업을 위해 SSE(Server-Sent Events) 스트림으로 장소 변경 및 멤버 변경 이벤트를 수신한다.

## 아키텍처 [coverage: high — 22 sources]

```
MyTuripScreen (내 튜립 탭)
  ├─ MyTuripTabRow          → 개인/공유 탭 전환
  ├─ MyTuripCard            → 튜립 카드 (이름, 장소수, 멤버수)
  └─ TuripAddBottomSheet    → 새 튜립 생성 바텀시트

MyTuripViewModel
  ├─ uiState: StateFlow<MyTuripUiState>
  ├─ uiEffect: Flow<MyTuripUiEffect>
  ├─ loadTurips()           → GET /v1/turips
  ├─ createTurip(name)      → POST /v1/turips
  ├─ deleteTurip(id)        → DELETE /v1/turips/{id}
  └─ exitTurip(id)          → DELETE /v1/turips/{id}/exit

TuripDetailScreen (튜립 상세)
  ├─ TuripInfoRow           → 튜립 이름, 공유 상태, 멤버수
  ├─ TuripPlaces            → 장소 목록 (추가/제거/순서 변경)
  ├─ TuripMapContent        → 장소 좌표 기반 Google Map
  ├─ MemberListSheet        → 멤버 아바타 목록 바텀시트
  └─ MoreOptionBottomSheet  → 이름 수정, 초대 링크 공유, 나가기

TuripDetailViewModel
  ├─ uiState: StateFlow<TuripDetailUiState>
  ├─ observeStream()        → SSE 스트림 구독 (FolderUpdate, MemberUpdate, Heartbeat)
  ├─ refreshOnFolderUpdate()→ 장소 변경 이벤트 수신 시 장소 목록 재조회
  └─ generateInvitationToken() → POST /v1/turips/{id}/invitation-tokens
```

## 의존 관계 [coverage: high — 22 sources]

```
MyTuripViewModel / TuripDetailViewModel
  └─ TuripRepository (domain interface)
       └─ DefaultTuripRepository (data)
            ├─ TuripRemoteDataSource → TuripService (Ktorfit, DefaultHttpClient)
            └─ TuripSseStreamDataSource → TuripStreamService (SseHttpClient)

TuripDetailScreen
  ├─ ObserveTuripStreamUseCase → TuripStreamHeartbeatManager → SSE 연결 유지
  └─ DeleteTuripUseCase
```

## API 표면 [coverage: high — 22 sources]

### `TuripService` (Ktorfit)

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| `getTurip(turipId)` | `GET /v1/turips/{turipId}` | 단일 튜립 조회 |
| `getTurips()` | `GET /v1/turips` | 내 튜립 목록 |
| `getTuripMembers(turipId)` | `GET /v1/turips/{turipId}/members` | 멤버 목록 |
| `postTurip(body)` | `POST /v1/turips` | 튜립 생성 |
| `patchTurip(turipId, body)` | `PATCH /v1/turips/{turipId}` | 이름 수정 |
| `deleteTurip(turipId)` | `DELETE /v1/turips/{turipId}` | 튜립 삭제 |
| `exitTurip(turipId)` | `DELETE /v1/turips/{turipId}/exit` | 튜립 나가기 |
| `getTuripsByPlaceId(placeId)` | `GET /v1/turips/turip-status?placeId=` | 장소별 튜립 현황 |
| `getTuripPlaces(turipId)` | `GET /v1/turips/places?turipId=` | 장소 목록 |
| `postTuripPlace(turipId, placeId)` | `POST /v1/turips/places` | 장소 추가 |
| `deleteTuripPlace(turipId, placeId)` | `DELETE /v1/turips/places` | 장소 제거 |
| `patchTuripPlaceOrder(turipId, body)` | `PATCH /v1/turips/places/turip-order` | 장소 순서 변경 |
| `putPlaceTurips(placeId, body)` | `PUT /v1/turips/places/{placeId}` | 장소-튜립 일괄 연결 |
| `postInvitationToken(turipId)` | `POST /v1/turips/{turipId}/invitation-tokens` | 초대 토큰 생성 |
| `postJoinTurip(turipId)` | `POST /v1/turips/{turipId}/join` | 튜립 참여 |
| `getInvitationInformation(token)` | `GET /v1/turips/invitation-tokens?token=` | 초대 정보 조회 |

## 데이터 [coverage: high — 22 sources]

### `Turip` (domain model)

```kotlin
data class Turip(
    val id: Long,
    val name: String,
    val isDefault: Boolean,   // 기본 튜립은 삭제/이름변경 불가
    val placeCount: Int,
    val memberCount: Int,
    val isShared: Boolean,
    val hasIncludePlace: Boolean,
)
```

### `TuripStreamEvent` (sealed interface)

| 타입 | 주요 필드 | 설명 |
|------|-----------|------|
| `Connect` | turipId, timestamp | SSE 연결 확립 |
| `FolderUpdate` | turipId, action, timestamp | 장소/폴더 변경 (PLACE_REORDERED/ADDED/DELETED/FOLDER_NAME_CHANGED 등) |
| `MemberUpdate` | turipId, action, memberCount, members | 멤버 변경 (MEMBER_JOINED/EXITED) |
| `Heartbeat` | timestamp | 연결 유지 |

### `MyTuripUiState`

```kotlin
data class MyTuripUiState(
    val isLoading: Boolean,
    val turips: ImmutableList<MyTuripModel>,
    val selectedTab: MyTuripTab,   // PERSONAL / SHARED
    val isDeleteMode: Boolean,
    val errorUiState: ErrorUiState,
)
```

## 주요 결정사항 [coverage: high — 22 sources]

1. **SSE 실시간 동기화**: 튜립 상세 진입 시 SSE 스트림을 구독하여 다른 멤버의 장소 변경을 실시간으로 반영한다. `TuripStreamHeartbeatManager`가 Heartbeat 누락 시 재연결을 처리한다.
2. **기본 튜립 보호**: `isDefault = true`인 튜립은 이름 변경과 삭제가 서버에서 거부된다(`ErrorType.Turip.DefaultTuripRenameNotAllowed`). UI에서도 해당 옵션을 숨긴다.
3. **삭제 모드 BackHandler**: `MyTuripScreen`에서 삭제 모드 활성화 시 `BackHandler`로 뒤로가기를 가로채어 삭제 모드를 먼저 해제한다.
4. **캐시 동기화**: `TuripDetailScreen` 이탈 시 변경된 memberCount와 isShared 상태를 `MyTuripScreen`의 캐시에 반영한다 (`RefreshScope`).

## 주의사항 [coverage: medium — 10 sources]

- SSE 연결은 `SseHttpClient`(socketTimeout = INFINITE)를 사용한다. 앱이 백그라운드로 전환되면 연결이 끊길 수 있으며, `TuripDetailScreen` 재진입 시 재연결된다.
- `FolderUpdate.UNKNOWN`과 `MemberUpdate.UNKNOWN`은 서버에서 알 수 없는 action 값이 내려올 때 매핑된다. 이 경우 UI 갱신이 발생하지 않는다.
- `putPlaceTurips`(PUT)는 장소 선택 화면(TuripSelection)에서 사용되며, 여러 튜립에 대한 일괄 연결 변경을 한 번의 요청으로 처리한다.

## 출처 [coverage: high — 22 sources]

- [TuripService.kt](../../app/src/main/java/com/on/turip/data/turip/service/TuripService.kt)
- [DefaultTuripRepository.kt](../../app/src/main/java/com/on/turip/data/turip/repository/DefaultTuripRepository.kt)
- [TuripSseParser.kt](../../app/src/main/java/com/on/turip/data/turip/TuripSseParser.kt)
- [DefaultTuripStreamService.kt](../../app/src/main/java/com/on/turip/data/turip/service/DefaultTuripStreamService.kt)
- [Turip.kt](../../app/src/main/java/com/on/turip/domain/turip/Turip.kt)
- [TuripStreamEvent.kt](../../app/src/main/java/com/on/turip/domain/turip/TuripStreamEvent.kt)
- [TuripStreamHeartbeatManager.kt](../../app/src/main/java/com/on/turip/domain/turip/TuripStreamHeartbeatManager.kt)
- [ObserveTuripStreamUseCase.kt](../../app/src/main/java/com/on/turip/domain/turip/ObserveTuripStreamUseCase.kt)
- [TuripRepository.kt](../../app/src/main/java/com/on/turip/domain/turip/repository/TuripRepository.kt)
- [MyTuripScreen.kt](../../app/src/main/java/com/on/turip/ui/compose/turip/MyTuripScreen.kt)
- [MyTuripViewModel.kt](../../app/src/main/java/com/on/turip/ui/compose/turip/MyTuripViewModel.kt)
- [MyTuripUiState.kt](../../app/src/main/java/com/on/turip/ui/compose/turip/MyTuripUiState.kt)
- [TuripDetailScreen.kt](../../app/src/main/java/com/on/turip/ui/compose/turipdetail/TuripDetailScreen.kt)
- [TuripDetailViewModel.kt](../../app/src/main/java/com/on/turip/ui/compose/turipdetail/TuripDetailViewModel.kt)
- [TuripPlaces.kt](../../app/src/main/java/com/on/turip/ui/compose/turipdetail/component/TuripPlaces.kt)
- [TuripMapContent.kt](../../app/src/main/java/com/on/turip/ui/compose/turipdetail/component/TuripMapContent.kt)
- [MemberListSheet.kt](../../app/src/main/java/com/on/turip/ui/compose/turipdetail/component/MemberListSheet.kt)
- [MoreOptionBottomSheet.kt](../../app/src/main/java/com/on/turip/ui/compose/turipdetail/component/MoreOptionBottomSheet.kt)
- [MyTuripNavKey.kt](../../app/src/main/java/com/on/turip/ui/compose/turip/navigation/MyTuripNavKey.kt)
- [TuripDetailNavKey.kt](../../app/src/main/java/com/on/turip/ui/compose/turipdetail/navigation/TuripDetailNavKey.kt)
- [TuripInvitationInformation.kt](../../app/src/main/java/com/on/turip/domain/turip/TuripInvitationInformation.kt)
- [TuripStreamResult.kt](../../app/src/main/java/com/on/turip/domain/turip/result/TuripStreamResult.kt)
