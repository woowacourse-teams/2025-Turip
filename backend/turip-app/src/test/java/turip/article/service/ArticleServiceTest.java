package turip.article.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;
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
import turip.article.controller.dto.response.ArticleResponse;
import turip.article.controller.dto.response.ArticlesResponse;
import turip.article.domain.Article;
import turip.article.domain.ArticleTag;
import turip.article.domain.Tag;
import turip.article.repository.ArticlePlaceRepository;
import turip.article.repository.ArticleRepository;
import turip.article.repository.ArticleTagRepository;
import turip.common.exception.custom.NotFoundException;
import turip.util.fixture.ArticleFixture;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks
    private ArticleService articleService;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleTagRepository articleTagRepository;

    @Mock
    private ArticlePlaceRepository articlePlaceRepository;

    @DisplayName("아티클 목록 조회 기능 테스트")
    @Nested
    class FindArticles {

        @DisplayName("thumbnailUrl이 없으면 기본 썸네일 URL로 채워서 반환한다")
        @Test
        void findArticles_withoutThumbnailUrl_returnsDefaultThumbnailUrl() {
            // given
            int size = 10;
            String defaultThumbnailUrl = "https://turip.com/static/default-thumbnail.png";
            ReflectionTestUtils.setField(articleService, "defaultThumbnailUrl", defaultThumbnailUrl);

            Article article = new Article("제목", "부제목", "본문", null, null, 1);
            ReflectionTestUtils.setField(article, "id", 1L);
            given(articleRepository.findFirstPageByIsPublishedTrue(PageRequest.of(0, size)))
                    .willReturn(new SliceImpl<>(List.of(article)));
            given(articleTagRepository.findAllByArticleIdIn(List.of(1L)))
                    .willReturn(List.of());

            // when
            ArticlesResponse response = articleService.findArticles(size, null);

            // then
            assertThat(response.articles()).hasSize(1);
            assertThat(response.articles().getFirst().thumbnailUrl()).isEqualTo(defaultThumbnailUrl);
        }

        @DisplayName("lastId가 없으면 첫 페이지를 조회한다")
        @Test
        void findArticles_withoutLastId_returnsFirstPage() {
            // given
            int size = 10;
            Article article = ArticleFixture.createWithId(1L, null);
            given(articleRepository.findFirstPageByIsPublishedTrue(PageRequest.of(0, size)))
                    .willReturn(new SliceImpl<>(List.of(article)));
            given(articleTagRepository.findAllByArticleIdIn(List.of(1L)))
                    .willReturn(List.of());

            // when
            ArticlesResponse response = articleService.findArticles(size, null);

            // then
            assertThat(response.articles()).hasSize(1);
            assertThat(response.loadable()).isFalse();
        }

        @DisplayName("lastId가 있으면 해당 아티클의 displayOrder 이후를 조회한다")
        @Test
        void findArticles_withLastId_returnsNextPage() {
            // given
            int size = 10;
            Long lastId = 1L;
            Article cursorArticle = ArticleFixture.createWithId(lastId, null);
            Article nextArticle = ArticleFixture.createWithId(2L, null);

            given(articleRepository.findByIdAndIsPublishedTrue(lastId))
                    .willReturn(Optional.of(cursorArticle));
            given(articleRepository.findNextPageByIsPublishedTrue(cursorArticle.getDisplayOrder(),
                    PageRequest.of(0, size)))
                    .willReturn(new SliceImpl<>(List.of(nextArticle)));
            given(articleTagRepository.findAllByArticleIdIn(List.of(2L)))
                    .willReturn(List.of());

            // when
            ArticlesResponse response = articleService.findArticles(size, lastId);

            // then
            assertThat(response.articles()).hasSize(1);
            assertThat(response.articles().getFirst().id()).isEqualTo(2L);
        }

        @DisplayName("존재하지 않는 lastId로 조회하면 NotFoundException을 발생시킨다")
        @Test
        void findArticles_withInvalidLastId_throwsNotFoundException() {
            // given
            int size = 10;
            Long lastId = 999L;
            given(articleRepository.findByIdAndIsPublishedTrue(lastId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> articleService.findArticles(size, lastId))
                    .isInstanceOf(NotFoundException.class);
        }

        @DisplayName("태그가 있으면 태그 이름 목록을 함께 반환한다")
        @Test
        void findArticles_withTags_returnsTagNames() {
            // given
            int size = 10;
            Article article = ArticleFixture.createWithId(1L, null);
            Tag tag = new Tag("드라이브");
            ArticleTag articleTag = new ArticleTag(article, tag);

            given(articleRepository.findFirstPageByIsPublishedTrue(PageRequest.of(0, size)))
                    .willReturn(new SliceImpl<>(List.of(article)));
            given(articleTagRepository.findAllByArticleIdIn(List.of(1L)))
                    .willReturn(List.of(articleTag));

            // when
            ArticlesResponse response = articleService.findArticles(size, null);

            // then
            assertThat(response.articles().getFirst().tags()).containsExactly("드라이브");
        }
    }

    @DisplayName("아티클 상세 조회 기능 테스트")
    @Nested
    class GetArticle {

        @DisplayName("공개된 아티클을 조회하면 상세 정보를 반환한다")
        @Test
        void getArticle_withPublishedArticle_returnsArticleResponse() {
            // given
            Long articleId = 1L;
            Article article = ArticleFixture.createWithId(articleId, null);

            given(articleRepository.findByIdAndIsPublishedTrue(articleId))
                    .willReturn(Optional.of(article));
            given(articleTagRepository.findAllByArticleId(articleId))
                    .willReturn(List.of());
            given(articlePlaceRepository.findAllByArticleId(articleId))
                    .willReturn(List.of());

            // when
            ArticleResponse response = articleService.getArticle(articleId);

            // then
            assertThat(response.id()).isEqualTo(articleId);
        }

        @DisplayName("존재하지 않거나 비공개인 아티클을 조회하면 NotFoundException을 발생시킨다")
        @Test
        void getArticle_withUnpublishedOrMissingArticle_throwsNotFoundException() {
            // given
            Long articleId = 999L;
            given(articleRepository.findByIdAndIsPublishedTrue(articleId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> articleService.getArticle(articleId))
                    .isInstanceOf(NotFoundException.class);
        }
    }
}
