---
topic: search
last_compiled: 2026-04-13
source_count: 17
status: active
---

# Search

## 목적 [coverage: high — 17 sources]

두 가지 독립적인 검색 흐름을 담당한다. (1) **키워드 검색**: 홈 화면의 `SearchTextField`에서 키워드를 입력하면 `SearchScreen`으로 진입하여 서버 API 검색 결과를 표시한다. 최근 검색어는 Room DB에 로컬 저장되어 `SearchHistoryList`로 표시된다. (2) **지역 탐색**: 홈 화면의 지역 칩을 탭하면 `RegionResultScreen`으로 진입하여 해당 지역 카테고리명 기준 콘텐츠 목록을 표시한다.

## 아키텍처 [coverage: high — 17 sources]

**흐름 1: 키워드 검색**

```
HomeScreen → SearchNavKey(keyword)
  └─ SearchScreen
       ├─ SearchAppBar        → 검색어 입력, 뒤로가기
       ├─ SearchHistoryList   → 최근 검색어 (포커스 시 오버레이)
       ├─ SearchResultList    → VideoInformationModel 목록
       └─ SearchEmptyView     → 결과 없음 표시

SearchViewModel (HiltViewModel)
  ├─ uiState: StateFlow<SearchUiState>
  ├─ uiEffect: Flow<SearchUiEffect>
  ├─ searchingWord: StateFlow<String>
  ├─ searchHistory: StateFlow<ImmutableList<SearchHistory>>
  ├─ initKeyword(keyword)    → 검색어 초기화 + 히스토리 로드 + 검색 실행 + 히스토리 저장
  ├─ loadByKeyword()         → count + 콘텐츠 목록 병렬 조회
  └─ deleteSearchHistory()   → Room에서 검색어 삭제
```

**흐름 2: 지역 탐색**

```
HomeScreen → RegionResultNavKey(regionCategoryName)
  └─ RegionResultScreen
       ├─ RegionResultAppBar  → 지역명 표시, 뒤로가기
       └─ SearchResultList    → VideoInformationModel 목록 (공유 컴포넌트)

RegionResultViewModel (HiltViewModel)
  ├─ uiState: StateFlow<RegionResultUiState>
  └─ loadContentsFromRegion(regionCategoryName) → count + 콘텐츠 목록 병렬 조회
```

## 의존 관계 [coverage: high — 17 sources]

```
SearchViewModel
  ├─ ContentRepository       → loadContentsSizeByKeyword, loadContentsByKeyword
  ├─ SearchHistoryRepository → createSearchHistory, loadRecentSearches, deleteSearch
  └─ SessionManager          → TokenExpired 시 switchToGuest()

RegionResultViewModel
  ├─ ContentRepository       → loadContentsSizeByRegion, loadContentsByRegion
  └─ SessionManager

SearchHistoryRepository
  └─ DefaultSearchHistoryRepository
       └─ SearchHistoryDataSource
            └─ SearchHistoryDao (Room)
                 └─ TuripDatabase (@Database version=1)

ContentRepository (검색 관련)
  └─ ContentService (Ktorfit)
       ├─ GET /v1/contents/keyword/count?keyword={keyword}
       ├─ GET /v1/contents/keyword?keyword={keyword}&size={size}&lastId={lastId}
       ├─ GET /v1/contents/count?regionCategory={name}
       └─ GET /v1/contents?regionCategory={name}&size={size}&lastId={lastId}
```

## API 표면 [coverage: high — 17 sources]

### NavKey

```kotlin
@Serializable data class SearchNavKey(val keyword: String) : NavKey
@Serializable data class RegionResultNavKey(val regionCategoryName: String) : NavKey
```

### `SearchHistoryRepository`

```kotlin
interface SearchHistoryRepository {
    suspend fun createSearchHistory(keyword: String): Result<Unit>
    suspend fun loadRecentSearches(limit: Int): Result<List<SearchHistory>>
    suspend fun deleteSearch(keyword: String): Result<Unit>
}
```

최대 `MAX_SEARCH_HISTORY_COUNT = 10`개를 표시한다.

### `SearchHistoryDao`

| 메서드 | SQL |
|--------|-----|
| `insertSearchHistory(entity)` | `INSERT OR REPLACE INTO search_history` |
| `getRecentSearchHistories(limit)` | `SELECT * ORDER BY history DESC LIMIT :limit` |
| `deleteSearch(keyword)` | `DELETE WHERE keyword = :keyword` |

## 데이터 [coverage: high — 17 sources]

### `SearchUiState` (sealed interface)

| 상태 | 설명 |
|------|------|
| `Loading` | 검색 중 |
| `Empty(keyword: String)` | 결과 없음 |
| `Success(videos, totalCount)` | `ImmutableList<VideoInformationModel>` + 총 건수 |
| `Error(errorUiState)` | 네트워크/서버 에러 |

### `SearchHistoryEntity` (Room)

```kotlin
@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey val keyword: String,           // 중복 시 REPLACE
    @ColumnInfo(name = "history") val history: Long,  // System.currentTimeMillis()
)
```

`TuripDatabase`는 version 1이며 현재 `SearchHistoryEntity` 하나만 포함한다.

### `VideoInformationModel` (UI, 공유)

`SearchResultList`가 키워드 검색과 지역 탐색 양쪽에서 공유하는 컴포넌트. 두 화면 모두 이 모델을 사용한다.

## 주요 결정사항 [coverage: high — 17 sources]

1. **로컬 검색 히스토리**: 검색어는 서버가 아닌 Room DB에 저장된다. `keyword`가 Primary Key이므로 동일 키워드는 `OnConflictStrategy.REPLACE`로 타임스탬프만 갱신된다.
2. **count + 목록 병렬 조회**: `loadByKeyword()`와 `loadContentsFromRegion()` 모두 `async`/`await()`로 건수와 목록을 동시에 조회한다.
3. **`SearchResultList` 공유**: 키워드 검색과 지역 탐색의 결과 목록 UI가 `SearchResultList` 컴포넌트를 공유한다.
4. **검색 히스토리 오버레이**: `isHistoryVisible` 플래그가 `true`이면 검색 결과 위에 `SearchHistoryList`를 오버레이로 표시한다.
5. **size=100 고정**: 현재 `size = 100, lastId = 0L`로 고정 호출한다. 페이지네이션은 미구현 상태다.

## 주의사항 [coverage: medium — 9 sources]

- `RegionResultUiState.Empty` 상태에는 UI 처리가 없다(`// TODO: 없는 경우는 없음 (현재)` 주석). 서버에서 빈 지역이 반환될 경우 빈 화면이 표시된다.
- `SearchViewModel.initKeyword()`는 `LaunchedEffect(Unit)`로 한 번만 실행된다. 재진입 시 재호출되지 않는다.
- `TuripDatabase` version이 1로 고정되어 있다. 스키마 변경 시 Migration이 정의되지 않으면 `fallbackToDestructiveMigration`이 필요하다.

## 출처 [coverage: high — 17 sources]

- [SearchScreen.kt](../../app/src/main/java/com/on/turip/ui/compose/search/keyword/SearchScreen.kt)
- [SearchViewModel.kt](../../app/src/main/java/com/on/turip/ui/compose/search/keyword/SearchViewModel.kt)
- [SearchUiState.kt](../../app/src/main/java/com/on/turip/ui/compose/search/keyword/SearchUiState.kt)
- [SearchHistoryList.kt](../../app/src/main/java/com/on/turip/ui/compose/search/keyword/component/SearchHistoryList.kt)
- [SearchNavKey.kt](../../app/src/main/java/com/on/turip/ui/compose/search/keyword/navigation/SearchNavKey.kt)
- [RegionResultScreen.kt](../../app/src/main/java/com/on/turip/ui/compose/search/regionresult/RegionResultScreen.kt)
- [RegionResultViewModel.kt](../../app/src/main/java/com/on/turip/ui/compose/search/regionresult/RegionResultViewModel.kt)
- [RegionResultUiState.kt](../../app/src/main/java/com/on/turip/ui/compose/search/regionresult/RegionResultUiState.kt)
- [RegionResultNavKey.kt](../../app/src/main/java/com/on/turip/ui/compose/search/regionresult/navigation/RegionResultNavKey.kt)
- [VideoInformationModel.kt](../../app/src/main/java/com/on/turip/ui/compose/search/model/VideoInformationModel.kt)
- [SearchHistoryEntity.kt](../../app/src/main/java/com/on/turip/data/searchhistory/SearchHistoryEntity.kt)
- [SearchHistoryDao.kt](../../app/src/main/java/com/on/turip/data/searchhistory/dao/SearchHistoryDao.kt)
- [DefaultSearchHistoryRepository.kt](../../app/src/main/java/com/on/turip/data/searchhistory/repository/DefaultSearchHistoryRepository.kt)
- [TuripDatabase.kt](../../app/src/main/java/com/on/turip/data/database/TuripDatabase.kt)
- [SearchHistoryRepository.kt](../../app/src/main/java/com/on/turip/domain/searchhistory/SearchHistoryRepository.kt)
- [SearchResultList.kt](../../app/src/main/java/com/on/turip/ui/compose/search/component/SearchResultList.kt)
- [SearchAppBar.kt](../../app/src/main/java/com/on/turip/ui/compose/search/keyword/component/SearchAppBar.kt)
