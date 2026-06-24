최종 판단

승인입니다. 구현 단계로 넘어가도 됩니다.

이번 버전은 실행 가능한 route 타입 제한, Supported/Unsupported 분리, CAS acknowledge, 활성 Collector 직렬화, iOS 프로세스 싱글턴, AlreadyHandling까지 포함한 DeepLink 우선권, at-least-once + effective-once 의미 정리까지 일관되게 맞물립니다. 테스트 범위와 제외 범위도 현실적입니다.

다만 구현 시 아래 두 가지는 반드시 확정해야 합니다.

1. startupGenerationId의 생성·갱신 주체를 정해야 합니다

현재 예시에는 변수가 등장하지만 누가 언제 변경하는지는 아직 정의되지 않았습니다.

계약을 다음처럼 고정하는 것이 좋습니다.

- 한 번의 Splash 시작부터 종료까지 동일한 generation 유지
- 새로운 startup/session initialization이 시작될 때만 증가
- recomposition, 딥링크 수신, 화면 전환만으로는 변경하지 않음
- 이전 generation의 미소비 Splash navigation effect는 폐기

가능하면 Splash effect에도 generation을 넣으십시오.

data class NavigateBySession(
val generationId: Long,
val destination: Destination,
)
if (effect.generationId != currentStartupGenerationId) {
return@collect
}

startupNavigationArbiter.navigateBySession {
navigator.goWithAllClear(effect.destination)
}

그렇지 않으면 이전 startup에서 발행된 NavigateToMain이 새 Arbiter에서 처리되면서 딥링크를 덮어쓰는 변형 Race가 생길 수 있습니다.

2. at-least-once 보장은 iOS Store에만 해당한다고 명시해야 합니다

문서상 Android는 warm-start Flow를 passthrough하고 acknowledge = { true }로 처리합니다. 따라서 Android Source는 pending 저장도 재전달도 하지 않으므로, acknowledge 전까지 at-least-once 계약을 충족하지 않습니다.

둘 중 하나를 선택하면 됩니다.

A. 전달 보장 설명을 “iOS DeepLinkStore 기준”이라고 명시
B. Android warm-start도 동일 DeepLinkStore를 사용

Android 동작 무변경이 우선이라면 A가 낫습니다.

예시:

iOS DeepLinkStore는 acknowledge 전까지 at-least-once 전달을 보장한다. Android passthrough source는 기존 warm-start 전달 특성을 유지하며 재전달을 보장하지 않는다. 두 플랫폼 모두 navigator idempotency를 적용한다.

추가할 테스트 2개
[ ] 이전 startup generation의 세션 navigation effect가 새 generation에서 폐기됨
[ ] Android passthrough source는 iOS Store의 at-least-once 계약 대상이 아님을 검증/문서화

이 두 부분은 설계 변경이라기보다 수명과 계약의 마지막 봉합입니다. 나머지는 충분히 구현 가능한 상태입니다. 레이스 컨디션은 이제 출입증 없이는 못 들어옵니다.