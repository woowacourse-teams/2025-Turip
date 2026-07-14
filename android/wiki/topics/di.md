---
topic: di
last_compiled: 2026-04-13
source_count: 12
status: active
---

# DI (Dependency Injection)

## 목적 [coverage: high — 12 sources]

Hilt(`@InstallIn(SingletonComponent)`)를 사용하여 앱 전체의 의존성을 관리한다. 네트워크 클라이언트, 데이터소스, 레포지토리, 서비스, 세션, 로컬 저장소, 네비게이션을 각각 독립된 모듈로 분리하여 제공한다.

## 아키텍처 [coverage: high — 12 sources]

| 모듈 | 설치 범위 | 주요 제공 |
|------|----------|-----------|
| `NetworkModule` | SingletonComponent | 3개 HttpClient, 2개 Ktorfit, Json |
| `ServiceModule` | SingletonComponent | Ktorfit 서비스 인터페이스 구현체 |
| `DataSourceModule` | SingletonComponent | DataSource 인터페이스 → Default 구현 바인딩 |
| `RepositoryModule` | SingletonComponent | Repository 인터페이스 → Default 구현 바인딩 |
| `AuthModule` | SingletonComponent | Auth 관련 바인딩 |
| `SessionModule` | SingletonComponent | TokenManager, AuthTokenCacheController 바인딩 |
| `LocalStorageModule` | SingletonComponent | Room DB, DataStore, SearchHistoryDao |
| `NavigationModule` | ActivityComponent | NavKeyProvider @IntoSet 멀티바인딩 11개 |
| `DispatcherModule` | SingletonComponent | CoroutineDispatcher (IO, Default, Main) |
| `TuripStreamServiceModule` | SingletonComponent | TuripStreamService 바인딩 |

## 의존 관계 [coverage: high — 12 sources]

```
NetworkModule
  ├─ @DefaultHttpClient HttpClient  → Bearer Auth 포함
  ├─ @NoAuthHttpClient HttpClient   → 인증 헤더 없음 (로그인/재발급 API용)
  ├─ @SseHttpClient HttpClient      → SSE plugin, socketTimeout=INFINITE
  ├─ @DefaultKtorfit Ktorfit        → DefaultHttpClient 사용
  └─ @NoAuthKtorfit Ktorfit         → NoAuthHttpClient 사용

ServiceModule
  ├─ TuripService         (@DefaultKtorfit)
  ├─ ContentService       (@DefaultKtorfit)
  ├─ BookmarkService      (@DefaultKtorfit)
  ├─ RegionService        (@DefaultKtorfit)
  ├─ AuthService          (@NoAuthKtorfit)
  ├─ MemberService        (@DefaultKtorfit)
  └─ GuestService         (@NoAuthKtorfit)

LocalStorageModule
  ├─ TuripDatabase (Room)
  ├─ SearchHistoryDao
  └─ DataStore<Preferences>
```

## API 표면 [coverage: high — 12 sources]

### 커스텀 Qualifier

```kotlin
// qualifier/AuthQualifiers.kt
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class DefaultHttpClient
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class NoAuthHttpClient
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class SseHttpClient
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class DefaultKtorfit
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class NoAuthKtorfit
```

### `NavigationModule` 멀티바인딩

```kotlin
@Module @InstallIn(ActivityComponent::class)
abstract class NavigationModule {
    @Binds @IntoSet abstract fun bindHomeNavKeyProvider(...): NavKeyProvider
    @Binds @IntoSet abstract fun bindMyTuripNavKeyProvider(...): NavKeyProvider
    // ... 총 11개
}
```

피처 추가 시 `@Binds @IntoSet` 한 줄로 화면을 자동 등록한다.

## 데이터 [coverage: medium — 4 sources]

`NetworkModule`의 타임아웃 설정:

| 설정 | 값 |
|------|-----|
| `connectTimeoutMillis` | 10,000ms |
| `socketTimeoutMillis` | 20,000ms (SSE는 INFINITE) |
| `requestTimeoutMillis` | 20,000ms (SSE는 INFINITE) |

SSE 재연결 시간: `reconnectionTime = 3,000ms`

로깅: debug 빌드에서 `LogLevel.ALL`, release 빌드에서 `LogLevel.NONE`. `PrettyLogger`가 JSON Body를 들여쓰기 형식으로 포맷하여 `Timber.d()`로 출력한다. 로그 태그: `"moongjenut"`.

`device-fid` 헤더: `FidProvider.cachedFid`를 `defaultRequest` 블록에서 모든 요청에 첨부한다.

## 주요 결정사항 [coverage: high — 12 sources]

1. **HttpClient 3분리**: 인증 포함(`DefaultHttpClient`), 인증 없음(`NoAuthHttpClient`), SSE 전용(`SseHttpClient`)으로 분리하여 각 목적에 맞는 설정을 독립적으로 유지한다.
2. **Lazy<AuthRepository>로 순환 참조 해소**: `NetworkModule`의 `headerInterceptor`에서 `AuthRepository`를 `Lazy<>`로 감싸 Hilt의 순환 의존성 에러를 우회한다.
3. **@IntoSet 멀티바인딩**: `NavigationModule`이 `List<NavKeyProvider>`를 `@IntoSet`으로 조립한다. 피처는 자신의 `NavKeyProvider`를 여기에 바인딩하여 자동 등록된다.
4. **DispatcherModule**: `@IoDispatcher`, `@DefaultDispatcher`, `@MainDispatcher` qualifier로 CoroutineDispatcher를 주입한다. ViewModel 테스트 시 교체 가능하다.

## 주의사항 [coverage: medium — 5 sources]

- `NetworkModule`에서 `@DefaultHttpClient`와 `@SseHttpClient` 모두 `headerInterceptor`(Bearer Auth)를 포함한다. 새 HttpClient 추가 시 인증 포함 여부를 명시적으로 결정해야 한다.
- `LocalStorageModule`은 `TuripDatabase`를 `@Singleton`으로 제공한다. Room DB 인스턴스는 앱 당 하나만 존재해야 한다.
- `NavigationModule`은 `ActivityComponent`에 설치된다. `SingletonComponent`의 의존성을 주입받을 수 있지만, 반대는 불가하다.

## 출처 [coverage: high — 12 sources]

- [NetworkModule.kt](../../app/src/main/java/com/on/turip/di/NetworkModule.kt)
- [ServiceModule.kt](../../app/src/main/java/com/on/turip/di/ServiceModule.kt)
- [DataSourceModule.kt](../../app/src/main/java/com/on/turip/di/DataSourceModule.kt)
- [RepositoryModule.kt](../../app/src/main/java/com/on/turip/di/RepositoryModule.kt)
- [AuthModule.kt](../../app/src/main/java/com/on/turip/di/AuthModule.kt)
- [SessionModule.kt](../../app/src/main/java/com/on/turip/di/SessionModule.kt)
- [LocalStorageModule.kt](../../app/src/main/java/com/on/turip/di/LocalStorageModule.kt)
- [NavigationModule.kt](../../app/src/main/java/com/on/turip/di/NavigationModule.kt)
- [DispatcherModule.kt](../../app/src/main/java/com/on/turip/di/DispatcherModule.kt)
- [TuripStreamServiceModule.kt](../../app/src/main/java/com/on/turip/di/TuripStreamServiceModule.kt)
- [AuthQualifiers.kt](../../app/src/main/java/com/on/turip/di/qualifier/AuthQualifiers.kt)
- [NetworkQualifiers.kt](../../app/src/main/java/com/on/turip/di/qualifier/NetworkQualifiers.kt)
