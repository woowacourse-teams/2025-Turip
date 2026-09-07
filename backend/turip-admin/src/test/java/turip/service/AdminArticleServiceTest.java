package turip.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;
import turip.account.domain.Account;
import turip.account.domain.TuripMember;
import turip.article.domain.Article;
import turip.article.domain.ArticlePlace;
import turip.article.domain.ArticleTag;
import turip.article.domain.Tag;
import turip.article.repository.ArticlePlaceRepository;
import turip.article.repository.ArticleRepository;
import turip.article.repository.ArticleTagRepository;
import turip.article.repository.TagRepository;
import turip.common.exception.custom.NotFoundException;
import turip.controller.dto.request.AdminArticleCreateRequest;
import turip.controller.dto.request.AdminArticleUpdateRequest;
import turip.controller.dto.response.AdminArticleResponse;
import turip.controller.dto.response.AdminArticlesResponse;
import turip.place.domain.Place;
import turip.place.repository.PlaceRepository;
import turip.util.fixture.AccountFixture;
import turip.util.fixture.ArticleFixture;
import turip.util.fixture.MemberFixture;
import turip.util.fixture.PlaceFixture;
import turip.util.fixture.TuripMemberFixture;

@ExtendWith(MockitoExtension.class)
class AdminArticleServiceTest {

    @InjectMocks
    private AdminArticleService adminArticleService;

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private ArticleTagRepository articleTagRepository;
    @Mock
    private ArticlePlaceRepository articlePlaceRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private PlaceRepository placeRepository;

    private TuripMember admin;

    @BeforeEach
    void setUp() {
        Account account = AccountFixture.createAdmin();
        admin = TuripMemberFixture.createTuripMember();
        ReflectionTestUtils.setField(admin, "member", MemberFixture.createCustomMember(account, "admin@turip.com", false));
    }

    @Test
    @DisplayName("태그와 연관 장소 없이 아티클을 생성한다.")
    void create1() {
        // given
        AdminArticleCreateRequest request = new AdminArticleCreateRequest(
                "제목", "부제목", "본문", null, false, List.of(), List.of()
        );

        when(articleRepository.findMinDisplayOrder()).thenReturn(Optional.empty());
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> {
            Article article = invocation.getArgument(0);
            ReflectionTestUtils.setField(article, "id", 1L);
            return article;
        });

        // when
        Long articleId = adminArticleService.create(request, admin);

        // then
        assertThat(articleId).isEqualTo(1L);
    }

    @Test
    @DisplayName("기존 태그는 재사용하고, 없는 태그는 새로 생성해서 연결한다. 태그 조회는 한번의 배치 쿼리로 수행한다.")
    void create2() {
        // given
        AdminArticleCreateRequest request = new AdminArticleCreateRequest(
                "제목", "부제목", "본문", null, false, List.of("여행", "행복"), List.of()
        );

        Tag existingTag = new Tag("여행");
        ReflectionTestUtils.setField(existingTag, "id", 10L);

        when(articleRepository.findMinDisplayOrder()).thenReturn(Optional.empty());
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> {
            Article article = invocation.getArgument(0);
            ReflectionTestUtils.setField(article, "id", 1L);
            return article;
        });
        when(tagRepository.findAllByNameIn(List.of("여행", "행복"))).thenReturn(List.of(existingTag));
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> {
            Tag tag = invocation.getArgument(0);
            ReflectionTestUtils.setField(tag, "id", 11L);
            return tag;
        });

        // when
        adminArticleService.create(request, admin);

        // then
        verify(tagRepository, never()).findByName(any(String.class));
        verify(tagRepository, times(1)).findAllByNameIn(List.of("여행", "행복"));
        verify(tagRepository, never()).save(existingTag);
        verify(tagRepository, times(1)).save(argThat(tag -> tag.getName().equals("행복")));
        verify(articleTagRepository, times(2)).save(any(ArticleTag.class));
    }

    @Test
    @DisplayName("존재하지 않는 placeId는 무시하고, 존재하는 place만 연결한다.")
    void create3() {
        // given
        AdminArticleCreateRequest request = new AdminArticleCreateRequest(
                "제목", "부제목", "본문", null, false, List.of(), List.of(1L, 999L)
        );

        Place existingPlace = PlaceFixture.createWithId(1L);

        when(articleRepository.findMinDisplayOrder()).thenReturn(Optional.empty());
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> {
            Article article = invocation.getArgument(0);
            ReflectionTestUtils.setField(article, "id", 1L);
            return article;
        });
        when(placeRepository.findAllById(List.of(1L, 999L))).thenReturn(List.of(existingPlace));

        // when
        adminArticleService.create(request, admin);

        // then
        verify(articlePlaceRepository, times(1)).save(any(ArticlePlace.class));
    }

    @DisplayName("아티클 목록 조회 기능 테스트")
    @Nested
    class FindArticles {

        @DisplayName("lastId가 없으면 공개·비공개 상관없이 첫 페이지를 조회한다")
        @Test
        void findArticles1() {
            // given
            int size = 10;
            Article publishedArticle = ArticleFixture.createWithId(1L, null);
            Article unpublishedArticle = ArticleFixture.createWithId(2L, null);

            when(articleRepository.findFirstPage(false, PageRequest.of(0, size)))
                    .thenReturn(new SliceImpl<>(List.of(unpublishedArticle, publishedArticle)));
            when(articleTagRepository.findAllByArticleIdIn(List.of(2L, 1L)))
                    .thenReturn(List.of());

            // when
            AdminArticlesResponse response = adminArticleService.findArticles(size, null);

            // then
            assertThat(response.articles()).hasSize(2);
        }

        @DisplayName("lastId가 있으면 해당 아티클의 displayOrder 이후를 조회한다")
        @Test
        void findArticles2() {
            // given
            int size = 10;
            Long lastId = 1L;
            Article cursorArticle = ArticleFixture.createWithId(lastId, null);
            Article nextArticle = ArticleFixture.createWithId(2L, null);

            when(articleRepository.findById(lastId)).thenReturn(Optional.of(cursorArticle));
            when(articleRepository.findNextPage(false, cursorArticle.getDisplayOrder(), PageRequest.of(0, size)))
                    .thenReturn(new SliceImpl<>(List.of(nextArticle)));
            when(articleTagRepository.findAllByArticleIdIn(List.of(2L)))
                    .thenReturn(List.of());

            // when
            AdminArticlesResponse response = adminArticleService.findArticles(size, lastId);

            // then
            assertThat(response.articles()).hasSize(1);
            assertThat(response.articles().getFirst().id()).isEqualTo(2L);
        }

        @DisplayName("존재하지 않는 lastId로 조회하면 NotFoundException을 발생시킨다")
        @Test
        void findArticles3() {
            // given
            Long lastId = 999L;
            when(articleRepository.findById(lastId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminArticleService.findArticles(10, lastId))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @DisplayName("아티클 상세 조회 기능 테스트")
    @Nested
    class GetArticle {

        @DisplayName("비공개 아티클도 상세 정보를 반환한다")
        @Test
        void getArticle1() {
            // given
            Long articleId = 1L;
            Article article = ArticleFixture.createWithId(articleId, null);

            when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
            when(articleTagRepository.findAllByArticleId(articleId)).thenReturn(List.of());
            when(articlePlaceRepository.findAllByArticleId(articleId)).thenReturn(List.of());

            // when
            AdminArticleResponse response = adminArticleService.getArticle(articleId);

            // then
            assertThat(response.id()).isEqualTo(articleId);
        }

        @DisplayName("존재하지 않는 아티클을 조회하면 NotFoundException을 발생시킨다")
        @Test
        void getArticle2() {
            // given
            Long articleId = 999L;
            when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminArticleService.getArticle(articleId))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @DisplayName("아티클 수정 기능 테스트")
    @Nested
    class Update {

        @DisplayName("존재하는 아티클을 수정하면 필드와 태그/장소가 갱신된다")
        @Test
        void update1() {
            // given
            Long articleId = 1L;
            Article article = ArticleFixture.createWithId(articleId, null);
            AdminArticleUpdateRequest request = new AdminArticleUpdateRequest(
                    "새 제목", "새 부제목", "새 본문", "https://turip.com/new.png", true,
                    List.of("여행"), List.of(1L)
            );

            Tag existingTag = new Tag("여행");
            ReflectionTestUtils.setField(existingTag, "id", 10L);
            Place existingPlace = PlaceFixture.createWithId(1L);

            when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
            when(tagRepository.findAllByNameIn(List.of("여행"))).thenReturn(List.of(existingTag));
            when(placeRepository.findAllById(List.of(1L))).thenReturn(List.of(existingPlace));
            when(articleTagRepository.findAllByArticleId(articleId)).thenReturn(List.of());
            when(articlePlaceRepository.findAllByArticleId(articleId)).thenReturn(List.of());

            // when
            AdminArticleResponse response = adminArticleService.update(articleId, request);

            // then
            assertThat(response.title()).isEqualTo("새 제목");
            assertThat(response.subtitle()).isEqualTo("새 부제목");
            assertThat(response.content()).isEqualTo("새 본문");
            assertThat(response.isPublished()).isTrue();
            verify(articleTagRepository, times(1)).deleteAllByArticleId(articleId);
            verify(articlePlaceRepository, times(1)).deleteAllByArticleId(articleId);
            verify(articleTagRepository, times(1)).save(any(ArticleTag.class));
            verify(articlePlaceRepository, times(1)).save(any(ArticlePlace.class));
        }

        @DisplayName("존재하지 않는 아티클을 수정하면 NotFoundException을 발생시킨다")
        @Test
        void update2() {
            // given
            Long articleId = 999L;
            AdminArticleUpdateRequest request = new AdminArticleUpdateRequest(
                    "새 제목", "새 부제목", "새 본문", null, true, List.of(), List.of()
            );
            when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminArticleService.update(articleId, request))
                    .isInstanceOf(NotFoundException.class);
        }
    }
}
