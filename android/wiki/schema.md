# Wiki Schema — Turip Android

## Topics

| Slug | Description | Aliases |
|------|-------------|---------|
| `project-overview` | 프로젝트 전체 구조, 빌드 설정, 기술 스택 | turip app, android project, build config |
| `navigation` | Navigation3 기반 화면 전환, Navigator, NavKeyProvider | routing, nav, backstack, navigator |
| `turip` | 튜립 CRUD, 장소 관리, SSE 실시간 스트림 | my turip, turip detail, SSE, 튜립 |
| `login-auth` | Google 로그인, JWT 발급, 토큰 자동 재발급 | login, auth, 로그인, google sign-in, OAuth |
| `session-token` | TokenManager, DataStore 토큰 영속화, Ktor 캐시 | session, token, TokenManager, 세션 |
| `home` | 홈 탭 — 지역 탐색, 인기 콘텐츠, 검색 진입 | 홈, region, content list |
| `search` | 키워드 검색, 지역 탐색 결과, 검색 히스토리 | 검색, keyword search, region result |
| `trip` | 콘텐츠 상세, WebView 영상, 장소 선택 | 콘텐츠, trip detail, webview, video |
| `invitation` | 딥링크 초대, 튜립 참여 | 초대, invite, deep link, invitation |
| `mypage` | 마이페이지, 프로필, 북마크, 설정 | 마이페이지, my page, profile, bookmark list |
| `design-system` | TuripTheme, 색상/타이포/간격 토큰, 공용 컴포넌트 | 디자인 시스템, theme, component, snackbar |
| `data-layer` | TuripResult, ErrorType, safeApiCall, 레포지토리 패턴 | 데이터 레이어, repository, DTO, mapper |
| `di` | Hilt 모듈, HttpClient 3분리, NavKeyProvider 멀티바인딩 | 의존성 주입, hilt, DI, module |
| `lint-rules` | 커스텀 Lint 규칙, UnnamedFunctionTypeArgument | lint, 린트, code style |

## Concepts

| Slug | Connects | Description |
|------|----------|-------------|
| `clean-architecture-pattern` | data-layer, di, turip, login-auth, session-token | Service→DataSource→Repository→UseCase→ViewModel 5계층 패턴 |
| `realtime-sync-pattern` | turip, session-token, invitation | SSE + 토큰 재발급 + 캐시 동기화로 실시간 상태 일관성 유지 |

## Naming Conventions

- Topic slug: `lowercase-kebab-case`
- Concept slug: `lowercase-kebab-case`
- Article sections (Korean): 목적, 아키텍처, 의존 관계, API 표면, 데이터, 주요 결정사항, 주의사항, 출처
- Coverage tags: `[coverage: high — N sources]`, `[coverage: medium — N sources]`, `[coverage: low — N sources]`

## Evolution Log

- **2026-04-13**: Initial schema generated from 13 topics, 2 concepts (first compile)
