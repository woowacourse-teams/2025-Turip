---
concept: Clean Architecture Pattern
last_compiled: 2026-04-13
topics_connected: [data-layer, di, turip, login-auth, session-token]
status: active
---

# Clean Architecture Pattern

## Pattern

단일 앱 모듈 내에서 `ui → domain → data` 의존성 방향을 일관되게 유지한다. 5개 이상의 피처(turip, login, bookmark, content, region 등) 모두가 동일한 구조를 따른다: Ktorfit `Service` → `DataSource(interface + Default impl)` → `Repository(interface + Default impl)` → domain `UseCase` → `ViewModel`.

## Instances

- **2026-04-13** in [turip](../topics/turip.md): `TuripService` → `TuripRemoteDataSource` → `DefaultTuripRepository` → `ObserveTuripStreamUseCase` → `TuripDetailViewModel`
- **2026-04-13** in [login-auth](../topics/login-auth.md): `AuthService` → `AuthRemoteDataSource` → `DefaultAuthRepository` → `LoginViewModel`
- **2026-04-13** in [data-layer](../topics/data-layer.md): `safeApiCall` + `TuripResult<T>`로 모든 피처의 에러 처리를 통일
- **2026-04-13** in [di](../topics/di.md): Hilt `@Binds`로 interface → Default 구현체를 주입하여 피처별 독립성 유지
- **2026-04-13** in [session-token](../topics/session-token.md): `TokenManager(domain) ← DefaultTokenManager(data)` 구조로 세션 관리

## What This Means

아키텍처 계층이 명확히 분리되어 있어 각 피처의 데이터 소스를 교체하거나 테스트 더블을 주입하기 쉽다. 반면 단일 모듈이므로 피처 간 의존성 차단은 컴파일러가 아닌 팀 컨벤션에 의존한다. 새 피처 추가 시 동일 패턴을 따르면 `NavigationModule`에 `NavKeyProvider` 한 줄만 추가해도 화면이 자동 등록된다.

## Sources

- [data-layer](../topics/data-layer.md)
- [di](../topics/di.md)
- [turip](../topics/turip.md)
- [login-auth](../topics/login-auth.md)
- [session-token](../topics/session-token.md)
