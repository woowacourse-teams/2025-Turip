---
topic: mypage
last_compiled: 2026-04-13
source_count: 14
status: active
---

# My Page

## 목적 [coverage: high — 14 sources]

마이페이지 탭의 메인 화면. 사용자 프로필, 북마크한 콘텐츠 목록, 앱 설정(문의, 버전 정보, 로그아웃, 회원 탈퇴)을 제공한다. 북마크 전체 목록은 `BookmarkContentListScreen`으로 이동해 확인할 수 있다.

## 아키텍처 [coverage: high — 14 sources]

```
MyPageScreen
  ├─ MyPageAppBar             → 타이틀
  ├─ ProfileSection           → 프로필 이미지, 닉네임
  ├─ MyPageBookmarkContentSection
  │    └─ MyPageBookmarkContentItem × N → 썸네일, 제목
  │         └─ "전체보기" → BookmarkContentListScreen
  ├─ MyPageSettingsSection
  │    ├─ MyPageSettingItem "문의하기"   → 이메일 앱 실행
  │    ├─ MyPageSettingItem "앱 버전"    → 버전 표시
  │    ├─ MyPageSettingItem "로그아웃"   → 다이얼로그 확인 후 logout()
  │    └─ MyPageSettingItem "회원 탈퇴"  → 다이얼로그 확인 후 withdraw()
  └─ Dialog (LogoutConfirm / WithdrawConfirm)

MyPageViewModel (HiltViewModel)
  ├─ uiState: StateFlow<MyPageUiState>
  ├─ uiEffect: Flow<MyPageUiEffect>
  ├─ loadMyPage()       → 프로필 + 북마크 목록 병렬 조회
  ├─ logout()           → TokenManager.clearTokens() + navigator.goWithAllClear(LoginNavKey)
  └─ withdraw()         → DELETE /v1/members/me + clearTokens() + 로그인 화면

BookmarkContentListScreen (별도 화면)
  └─ BookmarkContentListViewModel → GET /v2/bookmarks (페이징)
```

## 의존 관계 [coverage: high — 14 sources]

```
MyPageViewModel
  ├─ AccountRepository (domain)
  │    └─ DefaultAccountRepository → MemberService (GET /v1/members/me)
  ├─ BookmarkRepository (domain)
  │    └─ DefaultBookmarkRepository → BookmarkService (GET /v2/bookmarks, DELETE)
  ├─ TokenManager → clearTokens (로그아웃/탈퇴 시)
  ├─ SessionManager → switchToGuest (세션 초기화)
  └─ AppEnvironmentInfoProvider → 앱 버전, 패키지명

BookmarkContentListViewModel
  └─ BookmarkRepository → loadBookmarks(size, lastId) (커서 페이징)
```

## API 표면 [coverage: high — 14 sources]

### `MemberService` (계정 관련)

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| `getMember()` | `GET /v1/members/me` | 내 프로필 조회 |
| `deleteMember()` | `DELETE /v1/members/me` | 회원 탈퇴 |

### `BookmarkService`

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| `getBookmarks(size, lastId)` | `GET /v2/bookmarks` | 북마크 목록 (커서 페이징) |
| `postBookmark(contentId)` | `POST /v1/bookmarks` | 북마크 추가 |
| `deleteBookmark(contentId)` | `DELETE /v1/bookmarks/{contentId}` | 북마크 제거 |

## 데이터 [coverage: high — 14 sources]

### `MyPageUiState`

```kotlin
data class MyPageUiState(
    val isLoading: Boolean,
    val profile: ProfileModel?,
    val bookmarkedContents: ImmutableList<BookmarkContentModel>,
    val sectionState: MyPageSectionState,
    val errorUiState: ErrorUiState,
)
```

### `ProfileModel` (UI)

```kotlin
data class ProfileModel(
    val nickname: String,
    val profileImageUrl: String?,
)
```

### `MyPageDialogState`

```kotlin
sealed interface MyPageDialogState {
    data object None : MyPageDialogState
    data object LogoutConfirm : MyPageDialogState
    data object WithdrawConfirm : MyPageDialogState
}
```

### `AppEnvironmentInfoModel`

```kotlin
data class AppEnvironmentInfoModel(
    val versionName: String,    // BuildConfig.VERSION_NAME
    val packageName: String,
)
```

### `InquiryMail`

앱 내 문의하기는 이메일 Intent로 처리한다. `InquiryMail` 객체에 수신 주소, 제목, 기기 정보가 포함된 본문 템플릿이 정의되어 있다.

## 주요 결정사항 [coverage: high — 14 sources]

1. **프로필 + 북마크 병렬 조회**: `loadMyPage()`에서 `async`/`await()`로 두 API를 동시에 조회한다.
2. **로그아웃 = 토큰 초기화 + 전체 스택 클리어**: `logout()`은 `TokenManager.clearTokens()` 후 `navigator.goWithAllClear(LoginNavKey())`를 호출하여 모든 내비게이션 히스토리를 초기화한다.
3. **회원 탈퇴 서버 우선**: `withdraw()`는 서버 API 성공 후에만 로컬 토큰을 제거한다. 서버 실패 시 에러를 표시하고 로컬 상태는 유지한다.
4. **북마크 커서 페이징 (v2)**: `BookmarkContentListScreen`에서는 `GET /v2/bookmarks?size=&lastId=` 커서 기반 페이징을 사용한다.

## 주의사항 [coverage: medium — 7 sources]

- `MyPageScreen`에 표시되는 북마크는 미리보기용으로 최대 N개만 표시한다. 전체 목록은 `BookmarkContentListScreen`으로 이동해야 한다.
- `AppEnvironmentInfoProvider`는 `PackageManager`에서 `versionName`을 읽는다. debug 빌드에서는 `.debug` 접미사가 포함된다.
- 로그아웃/탈퇴 후 `navigator.goWithAllClear()`로 스택이 초기화되므로, 이후 뒤로가기는 앱 종료 확인으로 이어진다.

## 출처 [coverage: high — 14 sources]

- [MyPageScreen.kt](../../app/src/main/java/com/on/turip/ui/compose/mypage/MyPageScreen.kt)
- [MyPageViewModel.kt](../../app/src/main/java/com/on/turip/ui/compose/mypage/MyPageViewModel.kt)
- [MyPageUiState.kt](../../app/src/main/java/com/on/turip/ui/compose/mypage/MyPageUiState.kt)
- [MyPageDialogState.kt](../../app/src/main/java/com/on/turip/ui/compose/mypage/MyPageDialogState.kt)
- [MyPageSectionState.kt](../../app/src/main/java/com/on/turip/ui/compose/mypage/MyPageSectionState.kt)
- [ProfileSection.kt](../../app/src/main/java/com/on/turip/ui/compose/mypage/component/ProfileSection.kt)
- [MyPageBookmarkContentSection.kt](../../app/src/main/java/com/on/turip/ui/compose/mypage/component/MyPageBookmarkContentSection.kt)
- [MyPageSettingsSection.kt](../../app/src/main/java/com/on/turip/ui/compose/mypage/component/MyPageSettingsSection.kt)
- [AppEnvironmentInfoProvider.kt](../../app/src/main/java/com/on/turip/ui/compose/mypage/util/AppEnvironmentInfoProvider.kt)
- [AppEnvironmentInfoModel.kt](../../app/src/main/java/com/on/turip/ui/compose/mypage/model/AppEnvironmentInfoModel.kt)
- [InquiryMail.kt](../../app/src/main/java/com/on/turip/ui/compose/mypage/model/InquiryMail.kt)
- [BookmarkContentListScreen.kt](../../app/src/main/java/com/on/turip/ui/compose/bookmark/BookmarkContentListScreen.kt)
- [BookmarkContentListViewModel.kt](../../app/src/main/java/com/on/turip/ui/compose/bookmark/BookmarkContentListViewModel.kt)
- [MyPageNavKey.kt](../../app/src/main/java/com/on/turip/ui/compose/mypage/navigation/MyPageNavKey.kt)
