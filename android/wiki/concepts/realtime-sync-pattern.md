---
concept: Realtime Sync Pattern
last_compiled: 2026-04-13
topics_connected: [turip, session-token, invitation]
status: active
---

# Realtime Sync Pattern

## Pattern

SSE(Server-Sent Events)와 토큰 자동 재발급, 캐시 동기화를 조합하여 실시간 상태 일관성을 유지하는 패턴이 여러 토픽에서 반복된다. "서버 이벤트 → 로컬 상태 갱신 → UI 반영" 흐름이 핵심이며, 연결 끊김·토큰 만료·백그라운드 전환을 각각 다른 레이어에서 처리한다.

## Instances

- **2026-04-13** in [turip](../topics/turip.md): SSE 스트림(`TuripStreamHeartbeatManager`)으로 장소 변경·멤버 변경 이벤트를 수신하여 `TuripDetailViewModel.uiState`를 갱신. `FolderUpdate` 이벤트 수신 시 장소 목록 재조회.
- **2026-04-13** in [session-token](../topics/session-token.md): Ktor Bearer Auth의 `refreshTokens` 블록에서 자동 재발급. `Mutex`로 동시 재발급 직렬화. 완료 후 `AuthTokenCacheController.clear()`로 HTTP 캐시 초기화.
- **2026-04-13** in [turip](../topics/turip.md): `TuripDetailScreen` 이탈 시 변경된 `memberCount`/`isShared`를 `MyTuripScreen` 캐시에 `RefreshScope`로 반영.
- **2026-04-13** in [invitation](../topics/invitation.md): 초대 참여 후 `MyTuripScreen`의 튜립 목록을 자동 갱신하여 새로 참여한 튜립이 즉시 반영됨.

## What This Means

실시간 동기화가 UI 레이어(SSE 이벤트 구독), 네트워크 레이어(토큰 자동 재발급), 화면 간 상태 전달(RefreshScope)에 걸쳐 분산되어 있다. SSE 연결은 `SseHttpClient`(socketTimeout=INFINITE)를 사용하므로 백그라운드 전환 시 연결이 끊길 수 있다. 화면 재진입 시 재연결되는 구조가 안전망 역할을 한다. 토큰 재발급 실패 시 세션이 초기화되고 로그인 화면으로 전환되는 것이 최종 fallback이다.

## Sources

- [turip](../topics/turip.md)
- [session-token](../topics/session-token.md)
- [invitation](../topics/invitation.md)
