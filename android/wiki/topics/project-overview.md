---
topic: project-overview
last_compiled: 2026-04-13
source_count: 5
status: active
---

# Project Overview

## 목적 [coverage: high — 5 sources]

Turip Android 앱은 여행 콘텐츠 탐색 및 장소 기반 여행 계획 관리 서비스다. 사용자는 크리에이터가 제작한 여행 콘텐츠를 탐색하고, 마음에 드는 장소를 "튜립(Turip)" 폴더에 저장하여 여행 일정을 관리한다. Google Sign-In 기반 소셜 로그인과 게스트 모드를 지원한다.

## 아키텍처 [coverage: high — 5 sources]

**모듈 구조**

```
:app          — 앱 메인 모듈 (전체 소스)
:lint-rules   — 커스텀 Android Lint 규칙
```

**패키지 기반 클린 아키텍처** (단일 모듈 내):

```
com.on.turip
  ├─ ui/           — Jetpack Compose 화면 (Screen, ViewModel, UiState, UiEffect)
  │    └─ compose/ — 피처별 패키지 (home, turip, turipdetail, trip, login, ...)
  ├─ domain/       — 도메인 모델, UseCase, Repository 인터페이스
  ├─ data/         — Repository 구현체, DataSource, DTO, Mapper, Service(Ktorfit)
  ├─ navigation/   — Navigator, NavigationState, NavKeyProvider
  ├─ di/           — Hilt 모듈
  ├─ core/         — 공통 유틸 (TuripResult, ErrorType, SessionState, ApiPath)
  └─ common/       — FidProvider, Timber Tree
```

**빌드 설정**

| 항목 | 값 |
|------|-----|
| `compileSdk` | 36 |
| `minSdk` | 24 |
| `targetSdk` | 35 |
| `namespace` | `com.on.turip` |
| Java/Kotlin Target | Java 17 |
| 빌드 타입 | debug(`.debug` suffix, minify 없음), release(ProGuard + 리소스 축소) |

**주요 BuildConfig 필드**

| 필드 | 출처 |
|------|------|
| `BASE_URL` | local.properties (debug_base_url / release_base_url) |
| `CLIENT_ID` | local.properties (Google OAuth) |
| `APP_LINK_TURIP_INVITATION_HOST` | local.properties (딥링크 호스트) |

## 의존 관계 [coverage: high — 5 sources]

**핵심 라이브러리**

| 카테고리 | 라이브러리 |
|----------|-----------|
| UI | Jetpack Compose BOM, Material3, Activity Compose, ViewModel Compose |
| 네비게이션 | Navigation3 (androidx.navigation3.runtime/ui), lifecycle-viewmodel-navigation3 |
| DI | Hilt Android, hilt-navigation-compose |
| 네트워크 | Ktorfit, Ktor (OkHttp, ContentNegotiation, Auth, Logging, SSE) |
| 로컬 저장소 | Room (version 1), DataStore Preferences |
| 이미지 | Coil, coil-network-okhttp, coil-compose |
| Firebase | Analytics, Crashlytics NDK, Installations |
| 지도 | Google Maps (google.map) |
| 인증 | CredentialManager, play-services-auth, googleid |
| 직렬화 | kotlinx.serialization.json |
| 기타 | Timber, kotlinx.collections.immutable, LeakCanary(debug), AppUpdate |

## API 표면 [coverage: medium — 3 sources]

앱은 REST API 서버(`BASE_URL`)와 통신한다. API 버전:
- `v1/` — 대부분의 엔드포인트
- `v2/` — 북마크 목록(`/v2/bookmarks`), 토큰 재발급(`/v2/auth/reissue`)

모든 인증 요청에 `Authorization: Bearer {accessToken}` 헤더와 `device-fid: {firebaseInstallationId}` 헤더가 자동 첨부된다.

## 데이터 [coverage: medium — 3 sources]

**로컬 저장소**

| 저장소 | 용도 |
|--------|------|
| Room `TuripDatabase` (version 1) | 검색 히스토리 (`search_history` 테이블) |
| DataStore Preferences | access_token, refresh_token, device_fid |

**Firebase**

- `Crashlytics`: release 빌드에서 `TuripReleaseTree`가 ERROR 이상 로그를 Crashlytics로 전송
- `Installations`: `FidProvider`가 Firebase Installation ID를 발급받아 캐싱

## 주요 결정사항 [coverage: high — 5 sources]

1. **단일 모듈 클린 아키텍처**: 멀티 모듈이 아닌 패키지 기반으로 ui/domain/data를 분리한다. 모듈 간 빌드 시간 최적화보다 개발 속도를 우선한 선택이다.
2. **Navigation3 채택**: Jetpack Navigation Component 대신 Navigation3(`androidx.navigation3`)를 사용한다. 탭별 독립 백스택과 `@Serializable` NavKey 기반의 타입 안전 네비게이션을 구현한다.
3. **Ktorfit + Ktor**: Retrofit 대신 Kotlin-first HTTP 클라이언트인 Ktor와 Ktorfit을 사용한다. SSE, Bearer Auth, Logging 모두 Ktor plugin으로 처리한다.
4. **Ktlint**: 코드 스타일을 Ktlint로 강제한다. CI/빌드 시 위반 시 빌드 실패.
5. **LeakCanary**: debug 빌드에만 포함되어 메모리 누수를 자동 감지한다.

## 주의사항 [coverage: medium — 3 sources]

- `local.properties`와 `keystore.properties` 두 파일이 모두 필요하다. 누락 시 빌드 시 `error()`가 발생한다. 각 필수 키 목록은 `app/build.gradle.kts`의 `requireLocalProperty` / `requireKeystoreProperty` 호출을 참조한다.
- `compileSdk 36`은 현재(2026-04) 최신 Android SDK다. `targetSdk 35`와 1 차이가 있으므로 SDK 36 특정 동작에 주의가 필요하다.
- `VERSION_NAME`과 `VERSION_CODE`는 `gradle/libs.versions.toml`의 `versionName` / `versionCode`에서 읽는다. 릴리즈 시 이 파일을 수정해야 한다.

## 출처 [coverage: high — 5 sources]

- [settings.gradle.kts](../../settings.gradle.kts)
- [build.gradle.kts](../../build.gradle.kts)
- [app/build.gradle.kts](../../app/build.gradle.kts)
- [FidProvider.kt](../../app/src/main/java/com/on/turip/common/FidProvider.kt)
- [TuripReleaseTree.kt](../../app/src/main/java/com/on/turip/common/TuripReleaseTree.kt)
