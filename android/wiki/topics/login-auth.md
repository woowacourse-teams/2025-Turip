---
topic: login-auth
last_compiled: 2026-04-13
source_count: 18
status: active
---

# Login & Auth

## 목적 [coverage: high — 18 sources]

Google Sign-In(CredentialManager)을 통한 소셜 로그인과 게스트 모드 진입을 담당한다. 서버에 Google ID 토큰을 전달하여 JWT(access token + refresh token)를 발급받고, 만료 시 refresh token으로 자동 재발급한다. Ktor Auth plugin의 Bearer 방식으로 모든 인증 API 요청에 토큰을 자동 첨부한다.

## 아키텍처 [coverage: high — 18 sources]

```
LoginScreen
  ├─ GoogleLoginButton         → Google CredentialManager 로그인 시작
  └─ GuestModeSection          → 게스트 모드로 진입

LoginViewModel (HiltViewModel)
  ├─ uiState: StateFlow<LoginUiState>
  ├─ uiEffect: Flow<LoginUiEffect>
  ├─ loginWithGoogle()
  │    └─ GoogleCredentialManager.getGoogleIdToken()
  │         └─ AuthRemoteDataSource.loginWithGoogleIdToken(idToken)
  │              └─ POST /v1/auth/login (LoginIdTokenPostRequest)
  │                   → LoginJwtTokenResponse → TokenManager.setTokens()
  └─ loginAsGuest()
       └─ GuestRemoteDataSource.loginAsGuest()
            └─ POST /v1/auth/guest

토큰 자동 재발급 (NetworkModule.headerInterceptor):
  Ktor Auth bearer {
    loadTokens { TokenManager.currentTokens }
    refreshTokens {
      AuthRefreshRemoteDataSource.reissueToken(refreshToken)
      → POST /v2/auth/reissue
      → TokenManager.setTokens(newTokens)
    }
  }
```

## 의존 관계 [coverage: high — 18 sources]

```
LoginViewModel
  ├─ AuthRepository (domain)
  │    └─ DefaultAuthRepository (data)
  │         ├─ AuthRemoteDataSource → AuthService (Ktorfit, NoAuthKtorfit)
  │         └─ AuthRefreshRemoteDataSource → AuthService (Ktorfit, NoAuthKtorfit)
  ├─ MemberRepository (domain)
  │    └─ DefaultMemberRepository → MemberService (Ktorfit, DefaultKtorfit)
  ├─ GuestRepository (domain)
  │    └─ DefaultGuestRepository → GuestService (Ktorfit, NoAuthKtorfit)
  ├─ TokenManager (domain, @Singleton)
  └─ GoogleCredentialManager → Android CredentialManager API

NetworkModule (전역 자동 재발급)
  └─ Lazy<AuthRepository> → refreshTokens 블록에서 사용 (순환 참조 방지)
```

## API 표면 [coverage: high — 18 sources]

### `AuthService` (Ktorfit)

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| `postLogin(body)` | `POST /v1/auth/login` | Google ID 토큰으로 JWT 발급 |
| `postGuestLogin()` | `POST /v1/auth/guest` | 게스트 로그인 |
| `postReissueToken(body)` | `POST /v2/auth/reissue` | refresh token으로 재발급 |

### `MemberService` (Ktorfit)

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| `getMember()` | `GET /v1/members/me` | 내 정보 조회 |
| `deleteMember()` | `DELETE /v1/members/me` | 회원 탈퇴 |

### `AuthRepository` (domain interface)

```kotlin
interface AuthRepository {
    suspend fun loginWithGoogleIdToken(idToken: String): TuripResult<AuthTokens>
    suspend fun loginAsGuest(): TuripResult<AuthTokens>
    suspend fun requestTokens(refreshToken: String): TuripResult<AuthTokens>
}
```

### `GoogleCredentialManager`

```kotlin
suspend fun getGoogleIdToken(activityContext: Context): TuripResult<String>
// Google CredentialManager.getCredential() → GoogleIdTokenCredential.idToken
```

## 데이터 [coverage: high — 18 sources]

### `AuthTokens` (domain)

```kotlin
data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
)
```

### DTO

| DTO | 필드 |
|-----|------|
| `LoginIdTokenPostRequest` | `idToken: String`, `clientId: String` (BuildConfig.CLIENT_ID) |
| `LoginJwtTokenResponse` | `accessToken: String`, `refreshToken: String` |
| `ReissueTokenRequest` | `refreshToken: String` |
| `ReissueTokenResponse` | `accessToken: String`, `refreshToken: String` |

### `LoginUiState`

```kotlin
data class LoginUiState(
    val isLoading: Boolean,
    val errorUiState: ErrorUiState,
)
```

### `LoginUiEffect`

```kotlin
sealed interface LoginUiEffect {
    data object NavigateToHome : LoginUiEffect
    data class ShowError(val message: String) : LoginUiEffect
}
```

## 주요 결정사항 [coverage: high — 18 sources]

1. **NoAuthKtorfit 사용**: 로그인/재발급 API는 인증 헤더가 없는 `NoAuthKtorfit`을 통해 호출한다. `DefaultKtorfit`(Bearer 첨부)을 사용하면 이미 만료된 access token이 헤더에 붙어 401이 발생한다.
2. **Mutex + Lazy 재발급**: `NetworkModule.headerInterceptor`의 `refreshTokens` 블록에서 `Lazy<AuthRepository>`를 사용하여 순환 의존성을 회피한다. `TokenManager`의 `Mutex`가 동시 재발급을 직렬화한다.
3. **Concurrent refresh 방지**: `refreshTokens` 블록에서 `storedRefreshToken != tokenManager.currentTokens?.refreshToken`이면 다른 코루틴이 이미 재발급했거나 로그아웃한 것으로 판단하여 `ApiException.Auth`를 던진다.
4. **게스트 모드**: 별도 계정 없이 탐색만 가능한 게스트 토큰을 발급한다. 튜립 생성 등 인증 필요 기능 시도 시 로그인 화면으로 유도한다.
5. **CredentialManager**: 구식 GoogleSignIn SDK 대신 Android CredentialManager API를 사용한다. `BuildConfig.CLIENT_ID`가 local.properties에서 주입된다.

## 주의사항 [coverage: medium — 9 sources]

- `refreshTokens` 블록 내 예외는 `ApiException` 타입이어야 한다. `IOException`은 Ktor가 `Network` 에러로 처리하고, 그 외 예외는 Ktor Auth가 무시할 수 있다.
- `GoogleCredentialManager.getGoogleIdToken()` 실패 시(사용자 취소 포함) `TuripResult.Failure(ErrorType.Cancelled)` 또는 적절한 에러 타입을 반환한다. UI에서 취소를 별도 처리해야 한다.
- `ErrorType.Auth.InvalidIdToken`은 Google ID 토큰 자체가 유효하지 않을 때 서버가 반환한다. 네트워크 에러가 아닌 인증 에러이므로 재시도보다 재로그인 유도가 적절하다.

## 출처 [coverage: high — 18 sources]

- [LoginScreen.kt](../../app/src/main/java/com/on/turip/ui/compose/login/LoginScreen.kt)
- [LoginViewmodel.kt](../../app/src/main/java/com/on/turip/ui/compose/login/LoginViewmodel.kt)
- [LoginUiState.kt](../../app/src/main/java/com/on/turip/ui/compose/login/LoginUiState.kt)
- [GoogleLoginButton.kt](../../app/src/main/java/com/on/turip/ui/compose/login/component/GoogleLoginButton.kt)
- [GuestModeSection.kt](../../app/src/main/java/com/on/turip/ui/compose/login/component/GuestModeSection.kt)
- [GoogleCredentialManager.kt](../../app/src/main/java/com/on/turip/data/login/datasource/GoogleCredentialManager.kt)
- [DefaultAuthRemoteDataSource.kt](../../app/src/main/java/com/on/turip/data/login/datasource/DefaultAuthRemoteDataSource.kt)
- [DefaultAuthRefreshRemoteDataSource.kt](../../app/src/main/java/com/on/turip/data/login/datasource/DefaultAuthRefreshRemoteDataSource.kt)
- [DefaultGuestRemoteDataSource.kt](../../app/src/main/java/com/on/turip/data/login/datasource/DefaultGuestRemoteDataSource.kt)
- [DefaultMemberRemoteDataSource.kt](../../app/src/main/java/com/on/turip/data/login/datasource/DefaultMemberRemoteDataSource.kt)
- [DefaultAuthRepository.kt](../../app/src/main/java/com/on/turip/data/login/repository/DefaultAuthRepository.kt)
- [DefaultMemberRepository.kt](../../app/src/main/java/com/on/turip/data/login/repository/DefaultMemberRepository.kt)
- [DefaultGuestRepository.kt](../../app/src/main/java/com/on/turip/data/login/repository/DefaultGuestRepository.kt)
- [AuthService.kt](../../app/src/main/java/com/on/turip/data/login/service/AuthService.kt)
- [MemberService.kt](../../app/src/main/java/com/on/turip/data/login/service/MemberService.kt)
- [GuestService.kt](../../app/src/main/java/com/on/turip/data/login/service/GuestService.kt)
- [NetworkModule.kt](../../app/src/main/java/com/on/turip/di/NetworkModule.kt)
- [AuthModule.kt](../../app/src/main/java/com/on/turip/di/AuthModule.kt)
