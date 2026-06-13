# Turip Android Codebase Wiki — Navigation Guide

This project has a compiled knowledge wiki. Use it instead of scanning raw files.

## How to use this wiki

1. **Start at INDEX.md** — 토픽 테이블에서 관련 모듈을 찾는다
2. **1-3개 토픽 아티클을 읽는다** — 현재 작업과 관련된 것들
3. **Coverage 태그를 확인한다:**
   - `[coverage: high]` — 이 섹션을 신뢰하고 raw 파일은 건너뜀
   - `[coverage: medium]` — 개요는 정확하나 구현 세부사항은 원본 소스 확인 권장
   - `[coverage: low]` — 출처에 나열된 raw 파일을 직접 읽어야 함
4. **concepts/를 확인한다** — 여러 모듈에 걸친 아키텍처 패턴 파악 시
5. **raw 소스 파일은 코드 레벨 세부사항이 필요할 때만** 읽는다

## Topic Quick Reference

| 질문 | 읽을 토픽 |
|------|----------|
| 화면 전환이 어떻게 동작해? | [navigation](topics/navigation.md) |
| 튜립 CRUD/SSE 어떻게 돼? | [turip](topics/turip.md) |
| 로그인/토큰 재발급 흐름은? | [login-auth](topics/login-auth.md), [session-token](topics/session-token.md) |
| HTTP 클라이언트가 몇 개야? | [di](topics/di.md) |
| 에러 타입 체계가 어떻게 돼? | [data-layer](topics/data-layer.md) |
| 홈/검색은 어떻게 동작해? | [home](topics/home.md), [search](topics/search.md) |
| 초대 링크 딥링크 흐름은? | [invitation](topics/invitation.md) |
| 공통 컴포넌트 어디 있어? | [design-system](topics/design-system.md) |
| 의존성이 어떻게 주입돼? | [di](topics/di.md) |
| 빌드 설정/라이브러리 목록? | [project-overview](topics/project-overview.md) |

## When NOT to use the wiki

- 특정 함수의 정확한 시그니처 확인 → raw 파일 직접 읽기
- 버그 디버깅 (특정 라인 확인 필요) → raw 파일 직접 읽기
- `[coverage: low]` 섹션의 내용이 필요할 때

## Stats

Compiled: 2026-04-13 | Topics: 13 | Concepts: 2 | Sources analyzed: ~170 files
