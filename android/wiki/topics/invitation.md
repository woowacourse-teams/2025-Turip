---
topic: invitation
last_compiled: 2026-04-13
source_count: 10
status: active
---

# Invitation (초대)

## 목적 [coverage: high — 10 sources]

딥링크 기반 튜립 초대 시스템. 튜립 오너가 공유 링크를 생성하면, 수신자가 링크를 열었을 때 `InvitationEntryScreen`으로 진입하여 초대 정보를 확인하고 튜립에 참여할 수 있다. 로그인 상태, 이미 참여 여부, 유효하지 않은 토큰 등 다양한 진입 시나리오를 처리한다.

## 아키텍처 [coverage: high — 10 sources]

```
딥링크 (app link: appLinkTuripInvitationHost)
  → MainActivity 인터셉트
  → navigator.goWithAllClear(InvitationEntryNavKey(url))
       └─ InvitationEntryScreen
            ├─ 초대 정보 다이얼로그 (튜립 이름, 멤버수)
            ├─ 참여 확인 버튼 → POST /v1/turips/{turipId}/join
            └─ InvitationEntryViewModel

InvitationEntryViewModel (HiltViewModel)
  ├─ uiState: StateFlow<InvitationEntryUiState>
  ├─ uiEffect: Flow<InvitationEntryUiEffect>
  ├─ dialogState: StateFlow<InvitationEntryDialogState>
  ├─ fetchInvitationInfo(token)  → GET /v1/turips/invitation-tokens?token=
  └─ joinTurip(turipId)          → POST /v1/turips/{turipId}/join

초대 링크 생성 (TuripDetailScreen 내):
  → TuripDetailViewModel.generateInvitationToken(turipId)
  → POST /v1/turips/{turipId}/invitation-tokens → token
  → 딥링크 URL 조합 후 시스템 공유 시트 표시
```

## 의존 관계 [coverage: high — 10 sources]

```
InvitationEntryViewModel
  ├─ InvitationRepository (domain)
  │    └─ DefaultInvitationRepository (data)
  │         └─ InvitationRemoteDataSource → TuripService (Ktorfit)
  │              ├─ GET /v1/turips/invitation-tokens?token=
  │              └─ POST /v1/turips/{turipId}/join
  └─ SessionManager → 게스트 사용자인 경우 로그인 화면 유도

앱 링크 설정:
  app/build.gradle.kts
    manifestPlaceholders["appLinkTuripInvitationHost"] = local.properties 값
    BuildConfig.APP_LINK_TURIP_INVITATION_HOST
```

## API 표면 [coverage: high — 10 sources]

### `InvitationRepository` (domain interface)

```kotlin
interface InvitationRepository {
    suspend fun fetchInvitationInfo(token: String): TuripResult<TuripInvitationInformation>
    suspend fun joinTurip(turipId: Long): TuripResult<TuripJoinResult>
}
```

### `TuripService` (초대 관련 엔드포인트)

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| `postInvitationToken(turipId)` | `POST /v1/turips/{turipId}/invitation-tokens` | 초대 토큰 생성 |
| `getInvitationInformation(token)` | `GET /v1/turips/invitation-tokens?token=` | 토큰으로 초대 정보 조회 |
| `postJoinTurip(turipId)` | `POST /v1/turips/{turipId}/join` | 튜립 참여 |

### `InvitationEntryNavKey`

```kotlin
@Serializable
data class InvitationEntryNavKey(val invitationUrl: String) : NavKey
```

딥링크 URL 전체를 NavKey에 담아 전달한다. ViewModel에서 URL을 파싱하여 token을 추출한다.

## 데이터 [coverage: high — 10 sources]

### `TuripInvitationInformation` (domain)

```kotlin
data class TuripInvitationInformation(
    val turipId: Long,
    val turipName: String,
    val memberCount: Int,
    val isAlreadyJoined: Boolean,
)
```

### `InvitationEntryUiState`

```kotlin
data class InvitationEntryUiState(
    val isLoading: Boolean,
    val invitationInfo: TuripInvitationInformation?,
    val errorUiState: ErrorUiState,
)
```

### `InvitationEntryDialogState`

```kotlin
sealed interface InvitationEntryDialogState {
    data object None : InvitationEntryDialogState
    data object AlreadyJoined : InvitationEntryDialogState    // 이미 참여 중
    data object InvalidTarget : InvitationEntryDialogState    // 유효하지 않은 초대
    data object JoinSuccess : InvitationEntryDialogState      // 참여 성공
    data object RequireLogin : InvitationEntryDialogState     // 게스트 → 로그인 필요
}
```

## 주요 결정사항 [coverage: high — 10 sources]

1. **goWithAllClear로 딥링크 처리**: 초대 링크 진입 시 현재 내비게이션 히스토리 전체를 초기화하고 초대 화면으로 이동한다. 뒤로가기 시 홈으로 돌아가도록 `parentTopLevelKey = HomeNavKey`를 설정한다.
2. **게스트 사용자 처리**: 게스트로 초대 링크를 열면 `RequireLogin` 다이얼로그를 표시하고, 로그인 화면으로 유도한다. 로그인 후 원래 초대 URL을 다시 처리한다.
3. **`InvalidInvitationTarget`**: 토큰이 유효하지 않거나 만료된 경우 `InvalidInvitationTarget` 화면 또는 다이얼로그로 명확하게 안내한다.
4. **앱 링크 호스트 분리**: `APP_LINK_TURIP_INVITATION_HOST`를 `local.properties`에서 주입하여 debug/release 환경별로 다른 호스트를 사용할 수 있다.

## 주의사항 [coverage: medium — 6 sources]

- `isAlreadyJoined = true`인 경우 참여 버튼 대신 `AlreadyJoined` 다이얼로그를 표시한다. 이미 참여 중인 튜립으로의 참여 API 호출은 서버에서 에러를 반환할 수 있다.
- 초대 토큰은 서버 측에서 만료 시간이 있다. 만료된 토큰으로 진입하면 `getInvitationInformation`이 에러를 반환한다.
- 딥링크 URL 파싱은 ViewModel에서 수행한다. URL 형식이 변경될 경우 파싱 로직도 함께 수정해야 한다.

## 출처 [coverage: high — 10 sources]

- [InvitationEntryScreen.kt](../../app/src/main/java/com/on/turip/ui/compose/invitation/InvitationEntryScreen.kt)
- [InvitationEntryViewModel.kt](../../app/src/main/java/com/on/turip/ui/compose/invitation/InvitationEntryViewModel.kt)
- [InvitationEntryUiState.kt](../../app/src/main/java/com/on/turip/ui/compose/invitation/InvitationEntryUiState.kt)
- [InvitationEntryDialogState.kt](../../app/src/main/java/com/on/turip/ui/compose/invitation/InvitationEntryDialogState.kt)
- [InvalidInvitationTarget.kt](../../app/src/main/java/com/on/turip/ui/compose/invitation/InvalidInvitationTarget.kt)
- [InvitationEntryNavKey.kt](../../app/src/main/java/com/on/turip/ui/compose/invitation/navigation/InvitationEntryNavKey.kt)
- [DefaultInvitationRepository.kt](../../app/src/main/java/com/on/turip/data/invitation/repository/DefaultInvitationRepository.kt)
- [InvitationRepository.kt](../../app/src/main/java/com/on/turip/domain/invitation/repository/InvitationRepository.kt)
- [TuripService.kt](../../app/src/main/java/com/on/turip/data/turip/service/TuripService.kt)
- [app/build.gradle.kts](../../app/build.gradle.kts)
