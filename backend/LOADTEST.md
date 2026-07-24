# Loadtest 환경 가이드

loadtest 프로필은 성능 테스트를 위한 환경으로, 외부 API 의존성을 제거하고 Mock으로 대체하여 안정적인 부하 테스트를 가능하게 합니다.

## 외부 의존성 Mock 처리

### 1. FCM (Firebase Cloud Messaging) 알림

**구현체:** `LoadtestFirebaseClient`

- 실제 Firebase 서버를 호출하지 않음
- **네트워크 지연 시뮬레이션:** 150ms
- 모든 알림 요청을 성공으로 처리 (invalid token 없음)

```java
// turip-app/src/main/java/turip/infrastructure/client/LoadtestFirebaseClient.java
Thread.sleep(150); // FCM 호출 시뮬레이션
```

### 2. OAuth 로그인 (Google/Apple)

**설정 파일:** `application-loadtest.yml`

- Google/Apple client-id는 더미 값으로 설정
- **제약사항:** 실제 회원가입/로그인은 불가능
- IdTokenParser는 실제 검증을 수행하므로, 유효한 토큰 없이는 인증 불가

```yaml
google:
  client-id: loadtest-google-client-id
  ios-client-id: loadtest-google-ios-client-id

apple:
  client-id: loadtest-apple-client-id
```

### 3. JWT Secret Key

**설정 파일:** `application-loadtest.yml`

- 초대 링크용 JWT는 더미 시크릿 키 사용
- 테스트 전용이므로 보안에 민감하지 않음

```yaml
invitation:
  jwt:
    secret-key: loadtest-invitation-jwt-secret-key-for-testing-purposes-only
```

## 주의사항

### ⚠️ 회원가입/로그인 제약

loadtest 환경에서는 OAuth 설정이 더미 값이므로:

- ❌ 신규 회원가입 불가능
- ❌ Google/Apple 소셜 로그인 불가능
- ✅ 기존에 생성된 계정의 JWT 토큰을 사용한 API 호출은 가능

### 💡 테스트 데이터 준비 방법

부하 테스트를 위해서는 사전에 테스트 계정을 생성해두어야 합니다:

1. 개발/스테이징 환경에서 실제 OAuth로 계정 생성
2. JWT 액세스 토큰 발급
3. 발급된 토큰을 loadtest 시나리오에서 사용

## 성능 특성

### FCM 알림 지연 시간

현재 150ms로 설정되어 있으며, 실제 FCM 응답 시간과 다를 수 있습니다.

- **현재 설정:** 150ms
- **조정 방법:** `LoadtestFirebaseClient.java`의 `Thread.sleep()` 값 변경

```java
Thread.sleep(150); // 이 값을 조정
```

### 권장 조정 시나리오

- **낙관적 테스트:** 100ms (최적의 네트워크 환경)
- **보수적 테스트:** 200-300ms (국제 API 호출 고려)
- **실측 기반:** 운영 로그에서 실제 FCM 응답 시간 분석 후 설정

## 환경 활성화

```bash
# Gradle bootRun
./gradlew :turip-app:bootRun --args='--spring.profiles.active=loadtest'

# Docker
docker run -e SPRING_PROFILES_ACTIVE=loadtest ...
```

## 관련 파일

- `turip-app/src/main/resources/application-loadtest.yml` - 설정 파일
- `turip-app/src/main/java/turip/infrastructure/client/LoadtestFirebaseClient.java` - FCM Mock
- `turip-app/src/main/java/turip/infrastructure/client/FcmClient.java` - FCM 인터페이스
- `turip-app/src/main/java/turip/infrastructure/config/FirebaseConfig.java` - Firebase 설정 (loadtest 제외)
- `turip-app/src/main/java/turip/auth/token/GoogleTokenParser.java` - Google OAuth (loadtest 제외)
- `turip-app/src/main/java/turip/auth/token/AppleTokenParser.java` - Apple OAuth (loadtest 제외)
