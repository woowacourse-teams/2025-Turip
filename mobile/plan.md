# Apple 로그인 개선 계획

> 2026-07-12 코드 리뷰 기반. 대상: `feature/login/impl` (iOS `AppleCredentialManager` 중심)

## 배경

레이어링(Screen → ViewModel → UseCase → Repository → Datasource)과 nonce 검증 흐름(rawNonce 생성 → SHA-256 해시를 Apple 요청에 포함 → 서버에 idToken + rawNonce 전달)은 문제 없음. 아래는 방어가 부족한 지점과 구조적 중복에 대한 수정 계획.

---

## 1. 취소 판정 시 에러 도메인 확인 (우선순위: 높음)

**파일**: `feature/login/impl/src/iosMain/kotlin/com/on/turip/feature/login/impl/AppleCredentialManager.ios.kt:121`

**문제**: `didCompleteWithError.code == 1001`만으로 취소를 판정. 도메인 확인이 없어 다른 도메인의 code 1001 에러(예: NSURLError 계열)가 실제 실패인데도 조용히 무시됨.

**수정**:
- 하드코딩된 `APPLE_AUTHORIZATION_ERROR_CANCELED_CODE = 1001L` 상수 제거
- `didCompleteWithError.domain == ASAuthorizationErrorDomain && didCompleteWithError.code == ASAuthorizationErrorCanceled` 조건으로 교체
- `platform.AuthenticationServices.ASAuthorizationErrorDomain`, `ASAuthorizationErrorCanceled` import 추가

## 2. presentationAnchor 개선 (우선순위: 높음)

**파일**: `AppleCredentialManager.ios.kt:145`

**문제**:
- `UIApplication.windows`는 iOS 15+ deprecated이고 `firstOrNull()`이 keyWindow가 아닐 수 있음
- fallback `UIWindow()`는 화면에 붙지 않은 빈 윈도우 → 여기 걸리면 Apple 시트가 뜨지 않고 로딩 오버레이만 남음

**수정**:
- `UIApplication.sharedApplication.connectedScenes`에서 활성 `UIWindowScene`의 keyWindow를 찾는 방식으로 교체
- 못 찾을 경우 `windows` 기반 탐색을 fallback으로 유지 (`UIWindow()` 생성 fallback은 제거)

## 3. AppleCredentialManager 동시 호출 자체 방어 (우선순위: 중간)

**파일**: `AppleCredentialManager.ios.kt:36-75`

**문제**: `ASAuthorizationController.delegate`는 weak 참조라 delegate 생존은 `authorizationDelegate` 필드에 의존. `getCredential()`이 겹치면 두 번째 호출이 필드를 덮어써 첫 번째 delegate가 GC → 첫 코루틴이 영원히 resume되지 않음. 현재는 `LoginViewModel`의 `isLoading` 가드에만 의존하는 구조.

**수정**:
- 진행 중 요청이 있으면 즉시 `TuripResult.Failure(ErrorType.Cancelled)` 반환 (또는 진행 중 요청 완료 콜백에 새 요청이 간섭하지 못하도록 요청별 식별 후 자기 것일 때만 필드 초기화)
- `onCompleted`의 무조건적 `authorizationController = null` / `authorizationDelegate = null`도 자기 요청일 때만 수행하도록 변경

## 4. 취소/정리 처리 보강 (우선순위: 낮음)

**파일**: `AppleCredentialManager.ios.kt:69-72`

**문제**: `invokeOnCancellation`이 참조만 해제하고 진행 중인 인증 시트는 닫지 못함.

**수정**: iOS 16.4+ 사용 가능 시 `controller.cancel()` 호출 추가 (버전 체크 필요). 불가하면 현행 유지하되 주석으로 제약 명시.

## 5. Android actual 에러 의미 수정 (우선순위: 낮음)

**파일**: `feature/login/impl/src/androidMain/kotlin/com/on/turip/feature/login/impl/AppleCredentialManager.android.kt:17`

**문제**: 미지원 플랫폼인데 `ErrorType.Cancelled`를 반환 → 도달하게 되면 사용자 피드백 없이 조용히 사라짐. (현재는 버튼이 iOS에서만 노출되어 도달 불가)

**수정**: `ErrorType.Unknown`(또는 적절한 미지원 타입)으로 변경하고 cause의 `UnsupportedOperationException`은 유지. 도달 시 스낵바로 노출되도록.

## 6. 구조 리팩토링 — SocialCredential 통합 (우선순위: 중간, 별도 커밋)

**파일**: `LoginViewModel.kt:53-133`, `LoginUseCase.kt`

**문제**:
- `loginWithGoogle` / `loginWithApple`이 거의 복붙 (credential 획득 → useCase → migration 분기 → 취소/에러 처리)
- `LoginUseCase`도 `invoke(idToken)` / `loginWithApple(idToken, nonce)` 두 진입점으로 분기

**수정**:
```kotlin
sealed interface SocialCredential {
    data class Google(val idToken: String) : SocialCredential
    data class Apple(val idToken: String, val rawNonce: String) : SocialCredential
}
```
- `LoginUseCase`: `suspend operator fun invoke(credential: SocialCredential)` 단일 진입점으로 통합, 내부에서 provider별 `authRepository.login(...)` / `loginWithApple(...)` 분기
- `LoginViewModel`: 공통 `private fun login(getCredential: suspend () -> TuripResult<SocialCredential>)` 추출, `loginWithGoogle`/`loginWithApple`은 위임만
- 취소 처리 중복 정리: VM의 early-return과 `handleError`의 `UiError.Feature.Cancelled -> {}` 중 한쪽으로 통일 (로깅 구분이 필요하면 early-return 유지)

## 검토 후 보류 (수정 안 함)

- **nonce 생성 (`NSUUID` 2개 연결)**: `SecRandomCopyBytes`가 권장이지만 실무상 충분. 3번 작업 시 여유 있으면 함께 교체.
- **Apple 시트 타임아웃 부재**: 시스템 시트는 콜백 신뢰 가능. edge case 방어 필요성 낮음.
- **`LoginViewModel`의 MVIA 미준수**: 프로젝트 컨벤션(BaseViewModel + Intent/State/Effect)과 다르지만 로그인 화면 전체 재작성이 필요한 별도 작업. 팀 논의 후 결정.
- **CredentialManager를 VM 메서드 파라미터로 주입하는 방식**: KMP에서 Composable 컨텍스트가 필요한 SDK를 다루는 합리적 절충. 유지.
- **`Sha256.kt` 직접 구현**: FIPS 180-4 표준 구현으로 올바름. Google PKCE에서 동일 함수가 검증됨. 단, iosTest에 테스트 벡터 단위 테스트 추가는 고려할 만함.

---

## 작업 순서

1. 1번 + 2번 (iOS 안정성, 한 커밋)
2. 3번 + 4번 (+ 필요 시 nonce `SecRandomCopyBytes` 교체, 한 커밋)
3. 5번 (한 커밋)
4. 6번 리팩토링 (별도 커밋, `./gradlew ktlintFormat` 후 iOS 빌드 확인)
