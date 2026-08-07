---
topic: data-layer
last_compiled: 2026-04-13
source_count: 14
status: active
---

# Data Layer

## 목적 [coverage: high — 14 sources]

클린 아키텍처의 데이터 계층. Ktorfit 기반 REST API 호출, Room 로컬 DB, DataStore 영속 저장소를 추상화하여 도메인 계층에 제공한다. 모든 데이터 흐름은 `TuripResult<T>` sealed class로 통일하여 성공/실패를 타입 안전하게 처리한다.

## 아키텍처 [coverage: high — 14 sources]

**표준 피처 데이터 레이어 구조**:

```
{Feature}Service (Ktorfit interface)
  └─ {Feature}RemoteDataSource (interface)
       └─ Default{Feature}RemoteDataSource (impl)
            └─ {Feature}Repository (domain interface)
                 └─ Default{Feature}Repository (impl)
                      → 도메인 UseCase로 소비
```

**결과 타입 흐름**:

```
Ktorfit suspend fun → safeApiCall { } → TuripResult<T>
  ├─ Success(value: T)
  └─ Failure(errorType: ErrorType, cause: Throwable)
       └─ errorType: ErrorResponse.tag → ErrorType 매핑 (30+ 서버 에러 코드)
```

**로컬 저장소**:

```
Room TuripDatabase (version=1)
  └─ SearchHistoryDao → SearchHistoryEntity

DataStore<Preferences>
  └─ access_token, refresh_token, device_fid
```

## 의존 관계 [coverage: high — 14 sources]

```
data/ 패키지
  ├─ turip/       → TuripService, TuripSseStreamDataSource (SSE)
  ├─ content/     → ContentService (7 endpoints)
  ├─ bookmark/    → BookmarkService (4 endpoints, v1/v2)
  ├─ region/      → RegionService (1 endpoint)
  ├─ login/       → AuthService (3 endpoints), MemberService, GuestService
  ├─ invitation/  → InvitationRepository (TuripService 재사용)
  ├─ account/     → AccountRepository (MemberService 재사용)
  ├─ creator/     → CreatorRepository (ContentService 내 포함)
  ├─ searchhistory/ → SearchHistoryDao (Room)
  ├─ session/     → TokenManager, AuthTokenCacheController
  ├─ userstorage/ → DataStore 영속 저장
  ├─ database/    → TuripDatabase (Room)
  └─ result/      → ApiException, safeApiCall, ErrorResponse
```

## API 표면 [coverage: high — 14 sources]

### `TuripResult<T>` (core/result)

```kotlin
sealed class TuripResult<out T> {
    data class Success<out T>(val value: T) : TuripResult<T>()
    data class Failure(val errorType: ErrorType, val cause: Throwable) : TuripResult<Nothing>()
}
```

확장 함수: `onSuccess`, `onFailure`, `onFailureWithCause`, `map`, `mapCatching`, `fold`, `getOrElse`, `getOrNull`

### `ErrorType` 계층 (core/result)

| 카테고리 | 하위 타입 |
|---------|---------|
| `Auth` | InvalidIdToken, InvalidTokenSignature, InvalidToken, TokenExpired, TokenNotFound, UnAuthorized, Forbidden |
| `Device` | FidRequired |
| `Creator` | NotFound |
| `Content` | NotFound |
| `Place` | NotFound |
| `Region` | InvalidCategory |
| `Turip` | NotFound, DuplicatedName, BlankName, ExceededName, DefaultTuripRenameNotAllowed |
| `TuripPlace` | NotFound, DuplicatePlaceInTurip |
| `Bookmark` | DuplicateBookmarked |
| 기타 | Network, Cancelled, Unknown |

### `safeApiCall` 패턴 (data/result)

```kotlin
suspend fun <T> safeApiCall(block: suspend () -> T): TuripResult<T> {
    return try {
        TuripResult.Success(block())
    } catch (e: CancellationException) {
        throw e  // 코루틴 취소는 전파
    } catch (e: ApiException) {
        TuripResult.Failure(e.errorType, e)
    } catch (e: IOException) {
        TuripResult.Failure(ErrorType.Network, e)
    } catch (e: Exception) {
        TuripResult.Failure(ErrorType.Unknown, e)
    }
}
```

### API 버전별 엔드포인트 분포

| 버전 | 주요 용도 |
|------|-----------|
| `v1/` | turips, contents, regions, auth/login, members, bookmarks(추가/제거) |
| `v2/` | auth/reissue(토큰 재발급), bookmarks(목록 조회, 커서 페이징) |

### SSE 스트림

`DefaultTuripStreamService`가 `SseHttpClient.sse(urlString = "v1/turips/{turipId}/stream")`으로 연결한다. `TuripSseParser`가 SSE 이벤트 raw text를 `TuripStreamEvent`로 변환한다.

## 데이터 [coverage: high — 14 sources]

### Room: `TuripDatabase`

```kotlin
@Database(entities = [SearchHistoryEntity::class], version = 1)
abstract class TuripDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
}
```

### DataStore 키

```kotlin
val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
val DEVICE_FID_KEY = stringPreferencesKey("device_fid")
```

### `ApiPath` (core/network)

```kotlin
object ApiPath {
    const val V1 = "v1/"
    const val V2 = "v2/"
}
```

### `ApiException` (data/result)

```kotlin
sealed class ApiException : Exception() {
    data object Auth : ApiException()
    data object Network : ApiException()
    data class Error(val errorType: ErrorType) : ApiException()
}
```

서버 에러 응답의 `tag` 필드를 30개 이상의 상수와 매핑하여 `ErrorType`으로 변환한다.

## 주요 결정사항 [coverage: high — 14 sources]

1. **`TuripResult` 단일 결과 타입**: 모든 비동기 데이터 흐름이 `TuripResult<T>`를 반환한다. `Result<T>`나 `Flow<T>`를 직접 노출하지 않아 에러 처리 패턴을 통일한다.
2. **`safeApiCall` wrapper**: 모든 네트워크 호출을 `safeApiCall { }` 블록으로 감싸 예외를 `TuripResult.Failure`로 변환한다. `CancellationException`은 반드시 재전파하여 코루틴 취소가 정상 동작하도록 한다.
3. **서버 에러 코드 → `ErrorType` 매핑**: HTTP 에러 응답의 `tag` 필드를 서버 에러 코드 상수와 비교하여 타입 안전한 `ErrorType`으로 변환한다. 알 수 없는 코드는 `ErrorType.Unknown`으로 처리한다.
4. **DataSource + Repository 이중 추상화**: `DataSource`는 단일 데이터 소스(원격/로컬)를 추상화하고, `Repository`는 여러 `DataSource`를 조합하여 도메인 요구에 맞는 데이터를 제공한다.
5. **Mapper 분리**: DTO → 도메인 모델 변환은 `{Feature}Mapper.kt` 파일에서 확장 함수로 처리한다. 변환 로직이 Repository 구현체에 섞이지 않는다.

## 주의사항 [coverage: medium — 6 sources]

- `safeApiCall`에서 `CancellationException`을 다시 throw하지 않으면 코루틴 취소가 무시되어 메모리 누수 또는 잘못된 상태 갱신이 발생할 수 있다.
- `ErrorType.Auth.TokenExpired`는 자동 재발급 후에도 다시 발생하면 `ApiException.Auth`로 상위에 전파된다. UI 레이어에서 이를 감지하면 로그인 화면으로 강제 전환해야 한다.
- `TuripDatabase` version이 1로 고정되어 있다. 스키마 변경 시 Migration 객체가 없으면 기존 데이터가 손실된다(`fallbackToDestructiveMigration` 설정 여부 확인 필요).
- `Ktorfit`이 생성하는 구현체는 KSP 컴파일 타임에 생성된다. `ksp` 의존성 없이 빌드하면 구현체가 누락되어 런타임 에러가 발생한다.

## 출처 [coverage: high — 14 sources]

- [TuripResult.kt](../../app/src/main/java/com/on/turip/core/result/TuripResult.kt)
- [ErrorType.kt](../../app/src/main/java/com/on/turip/core/result/ErrorType.kt)
- [TuripResultExtensions.kt](../../app/src/main/java/com/on/turip/core/result/TuripResultExtensions.kt)
- [ApiPath.kt](../../app/src/main/java/com/on/turip/core/network/ApiPath.kt)
- [TuripDatabase.kt](../../app/src/main/java/com/on/turip/data/database/TuripDatabase.kt)
- [DefaultTokenManager.kt](../../app/src/main/java/com/on/turip/data/session/DefaultTokenManager.kt)
- [DefaultUserStorageLocalDataSource.kt](../../app/src/main/java/com/on/turip/data/userstorage/datasource/DefaultUserStorageLocalDataSource.kt)
- [TuripService.kt](../../app/src/main/java/com/on/turip/data/turip/service/TuripService.kt)
- [DefaultTuripStreamService.kt](../../app/src/main/java/com/on/turip/data/turip/service/DefaultTuripStreamService.kt)
- [TuripSseParser.kt](../../app/src/main/java/com/on/turip/data/turip/TuripSseParser.kt)
- [TuripMapper.kt](../../app/src/main/java/com/on/turip/data/turip/TuripMapper.kt)
- [DefaultTuripRepository.kt](../../app/src/main/java/com/on/turip/data/turip/repository/DefaultTuripRepository.kt)
- [NetworkModule.kt](../../app/src/main/java/com/on/turip/di/NetworkModule.kt)
- [LocalStorageModule.kt](../../app/src/main/java/com/on/turip/di/LocalStorageModule.kt)
