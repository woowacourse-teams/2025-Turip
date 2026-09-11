package turip.article.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import turip.article.domain.Article;
import turip.common.configuration.JpaAuditingConfiguration;

@ActiveProfiles({"test", "h2"})
@DataJpaTest
@Import(JpaAuditingConfiguration.class)
class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Article createAndPersist(String title, int displayOrder, boolean isPublished) {
        Article article = new Article(title, "부제목", "본문", null, null, displayOrder, isPublished);
        entityManager.persist(article);
        return article;
    }

    @DisplayName("FindFirstPage 단위테스트")
    @Nested
    class FindFirstPage {

        @DisplayName("onlyPublished가 true면 displayOrder 오름차순으로 공개된 아티클만 조회한다")
        @Test
        void findFirstPage1() {
            createAndPersist("공개1", 2, true);
            createAndPersist("공개2", 1, true);
            createAndPersist("비공개", 0, false);
            entityManager.flush();

            Slice<Article> result = articleRepository.findFirstPage(true, PageRequest.of(0, 10));

            List<Article> content = result.getContent();
            assertThat(content).hasSize(2);
            assertThat(content.get(0).getTitle()).isEqualTo("공개2");
            assertThat(content.get(1).getTitle()).isEqualTo("공개1");
        }

        @DisplayName("onlyPublished가 false면 displayOrder 오름차순으로 공개·비공개 모두 조회한다")
        @Test
        void findFirstPage2() {
            createAndPersist("공개", 2, true);
            createAndPersist("비공개", 1, false);
            entityManager.flush();

            Slice<Article> result = articleRepository.findFirstPage(false, PageRequest.of(0, 10));

            List<Article> content = result.getContent();
            assertThat(content).hasSize(2);
            assertThat(content.get(0).getTitle()).isEqualTo("비공개");
            assertThat(content.get(1).getTitle()).isEqualTo("공개");
        }
    }

    @DisplayName("FindNextPage 단위테스트")
    @Nested
    class FindNextPage {

        @DisplayName("onlyPublished가 true면 커서로 넘긴 displayOrder보다 큰 공개 아티클만 조회한다")
        @Test
        void findNextPage1() {
            createAndPersist("공개1", 1, true);
            createAndPersist("공개2", 2, true);
            createAndPersist("공개3", 3, true);
            entityManager.flush();

            Slice<Article> result = articleRepository.findNextPage(true, 1, PageRequest.of(0, 10));

            List<Article> content = result.getContent();
            assertThat(content).hasSize(2);
            assertThat(content.get(0).getTitle()).isEqualTo("공개2");
            assertThat(content.get(1).getTitle()).isEqualTo("공개3");
        }

        @DisplayName("onlyPublished가 true면 비공개 아티클은 커서 조건을 만족해도 조회되지 않는다")
        @Test
        void findNextPage2() {
            createAndPersist("공개1", 1, true);
            createAndPersist("비공개", 2, false);
            entityManager.flush();

            Slice<Article> result = articleRepository.findNextPage(true, 1, PageRequest.of(0, 10));

            assertThat(result.getContent()).isEmpty();
        }

        @DisplayName("onlyPublished가 false면 커서로 넘긴 displayOrder보다 큰 아티클을 공개·비공개 상관없이 조회한다")
        @Test
        void findNextPage3() {
            createAndPersist("공개", 1, true);
            createAndPersist("비공개", 2, false);
            entityManager.flush();

            Slice<Article> result = articleRepository.findNextPage(false, 1, PageRequest.of(0, 10));

            List<Article> content = result.getContent();
            assertThat(content).hasSize(1);
            assertThat(content.get(0).getTitle()).isEqualTo("비공개");
        }
    }
}
