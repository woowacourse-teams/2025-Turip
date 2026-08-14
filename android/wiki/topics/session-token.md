---
topic: session-token
last_compiled: 2026-04-13
source_count: 12
status: active
---

# Session & Token

## 목적 [coverage: high — 12 sources]

JWT 토큰(access + refresh)의 저장·조회·삭제를 단일 진입점(`TokenManager`)으로 통합 관리한다. 인메모리 캐시와 DataStore Preferences를 동기화하여, 앱 재시작 시 DataStore에서 토큰을 복원하고 실행 중에는 메모리에서 빠르게 접근한다. Ktor Bearer Auth의 캐시도 함께 초기화하여 만료된 토큰이 재사용되지 않도록 한다.

## 아키텍처 [coverage: high — 12 sources]

```
TokenManager (domain interface, @Singleton)
  └─ DefaultTokenManager (data/session)
       ├─ @Volatile _currentTokens: AuthTokens?   → 인메모리 캐시
       ├─ Mutex                                    → 코루틴 직렬화
       ├─ UserStorageRepository                    → DataStore 읽기/쓰기
       └─ AuthTokenCacheController                 → Ktor 캐시 초기화

AuthTokenCacheController (domain interface)
  └─ DefaultAuthTokenCacheController (data/session)
       └─ @DefaultHttpClient HttpClient.authProvider<BearerAuthProvider>().clearToken()

UserStorageRepository (domain interface)
  └─ DefaultUserStorageRepository (data/userstorage)
       └─ UserStorageLocalDataSource
            └─ DefaultUserStorageLocalDataSource
                 └─ DataStore<Preferences>
                      ├─ stringPreferencesKey("device_fid")
                      ├─ stringPreferencesKey("access_token")
                      └─ stringPreferencesKey("refresh_token")
```

## 의존 관계 [coverage: high — 12 sources]

```
DefaultTokenManager
  ├─ UserStorageRepository → DataStore (로컬 영속 저장)
  └─ AuthTokenCacheController → Ktor Auth BearerAuthProvider (HTTP 캐시 초기화)

TokenManager 사용처:
  ├─ NetworkModule.headerInterceptor (loadTokens, refreshTokens)
  ├─ LoginViewModel (setTokens — 로그인 성공 시)
  ├─ SessionManager / SessionUseCase (clearTokens — 로그아웃/탈퇴 시)
  └─ di/SessionModule (Hilt 바인딩)

FidProvider (common)
  └─ DataStore (device_fid 캐싱 — 모든 요청 헤더에 device-fid 첨부)
```

## API 표면 [coverage: high — 12 sources]

### `TokenManager` (domain interface)

```kotlin
interface TokenManager {
    val currentTokens: AuthTokens?          // 인메모리 즉시 접근 (null = 미로그인)
    suspend fun initialize()                 // 앱 시작 시 DataStore에서 토큰 복원
    suspend fun setTokens(tokens: AuthTokens): Result<Unit>   // 저장 + 캐시 초기화
    suspend fun clearTokens(): Result<Unit>  // 삭제 + 캐시 초기화
}
```

### `AuthTokenCacheController` (domain interface)

```kotlin
interface AuthTokenCacheController {
    fun clear()   // Ktor BearerAuthProvider.clearToken() 호출
}
```

### `UserStorageRepository` (domain interface, 토큰 관련)

```kotlin
suspend fun loadAccessToken(): Result<String?>
suspend fun loadRefreshToken(): Result<String?>
suspend fun createTokens(tokens: AuthTokens): Result<Unit>
suspend fun clearTokens(): Result<Unit>
```

## 데이터 [coverage: high — 12 sources]

### DataStore 키

| 키 | 타입 | 용도 |
|----|------|------|
| `access_token` | `stringPreferencesKey` | JWT access token |
| `refresh_token` | `stringPreferencesKey` | JWT refresh token |
| `device_fid` | `stringPreferencesKey` | Firebase Installation ID (device-fid 헤더) |

### `SessionState` (core/session)

```kotlin
sealed interface SessionState {
    data object LoggedIn : SessionState
    data object Guest : SessionState
    data object Unknown : SessionState   // 초기화 전
}
```

앱 시작 시 `TokenManager.initialize()` 이후 토큰 존재 여부에 따라 `LoggedIn` 또는 `Guest`로 전환된다.

## 주요 결정사항 [coverage: high — 12 sources]

1. **Volatile + Mutex 조합**: `@Volatile`로 읽기는 즉시 반환(잠금 없음), `Mutex.withLock`으로 쓰기는 직렬화한다. `currentTokens` getter는 Mutex 없이 접근 가능하여 Ktor `loadTokens`에서 suspend 없이 사용된다.
2. **2-tier 저장소**: 인메모리 캐시(_currentTokens)가 1차, DataStore가 2차다. Ktor Auth의 `loadTokens`는 메모리에서 즉시 반환하여 비동기 overhead를 없앤다.
3. **Ktor 캐시 동기화**: `setTokens`와 `clearTokens` 모두 `authCacheController.clear()`를 호출하여 Ktor가 캐싱한 BearerTokens를 무효화한다. 이를 생략하면 만료된 토큰이 다음 요청에 재사용된다.
4. **initialize() 단일 호출**: `Application.onCreate()`에서 `TokenManager.initialize()`를 한 번만 호출한다. 이후 `currentTokens`는 항상 최신 상태를 반영한다.
5. **device-fid 분리**: Firebase Installation ID는 `FidProvider`가 별도로 관리하며, 모든 HTTP 요청의 `device-fid` 헤더에 첨부된다. 로그인 여부와 무관하게 항상 포함된다.

## 주의사항 [coverage: medium — 7 sources]

- `TokenManager.initialize()`가 완료되기 전에 `currentTokens`에 접근하면 `null`이 반환된다. Splash 화면에서 초기화 완료를 기다려야 한다.
- `clearTokens()`는 DataStore 삭제가 실패해도 인메모리 토큰은 이미 `null`로 설정된 상태다. 반환된 `Result.failure`를 무시하면 로컬 저장소와 메모리 상태가 불일치할 수 있다.
- `DefaultAuthTokenCacheController`는 `Provider<HttpClient>`를 사용하여 `HttpClient` 초기화 이전에 `AuthTokenCacheController`가 주입되는 순환 의존성을 회피한다.

## 출처 [coverage: high — 12 sources]

- [DefaultTokenManager.kt](../../app/src/main/java/com/on/turip/data/session/DefaultTokenManager.kt)
- [DefaultAuthTokenCacheController.kt](../../app/src/main/java/com/on/turip/data/session/DefaultAuthTokenCacheController.kt)
- [TokenManager.kt](../../app/src/main/java/com/on/turip/domain/session/TokenManager.kt)
- [AuthTokenCacheController.kt](../../app/src/main/java/com/on/turip/domain/session/AuthTokenCacheController.kt)
- [SessionState.kt](../../app/src/main/java/com/on/turip/core/session/SessionState.kt)
- [DefaultUserStorageRepository.kt](../../app/src/main/java/com/on/turip/data/userstorage/repository/DefaultUserStorageRepository.kt)
- [DefaultUserStorageLocalDataSource.kt](../../app/src/main/java/com/on/turip/data/userstorage/datasource/DefaultUserStorageLocalDataSource.kt)
- [UserStorageRepository.kt](../../app/src/main/java/com/on/turip/domain/userstorage/repository/UserStorageRepository.kt)
- [UserStorageMapper.kt](../../app/src/main/java/com/on/turip/data/userstorage/UserStorageMapper.kt)
- [SessionModule.kt](../../app/src/main/java/com/on/turip/di/SessionModule.kt)
- [LocalStorageModule.kt](../../app/src/main/java/com/on/turip/di/LocalStorageModule.kt)
- [FidProvider.kt](../../app/src/main/java/com/on/turip/common/FidProvider.kt)
