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
        Article article = new Article(title, "부제목", "본문", null, null, displayOrder);
        if (isPublished) {
            article.publish();
        }
        entityManager.persist(article);
        return article;
    }

    @DisplayName("FindFirstPageByIsPublishedTrue 단위테스트")
    @Nested
    class FindFirstPageByIsPublishedTrue {

        @DisplayName("displayOrder 오름차순으로 공개된 아티클만 조회한다")
        @Test
        void findFirstPageByIsPublishedTrue1() {
            createAndPersist("공개1", 2, true);
            createAndPersist("공개2", 1, true);
            createAndPersist("비공개", 0, false);
            entityManager.flush();

            Slice<Article> result = articleRepository.findFirstPageByIsPublishedTrue(PageRequest.of(0, 10));

            List<Article> content = result.getContent();
            assertThat(content).hasSize(2);
            assertThat(content.get(0).getTitle()).isEqualTo("공개2");
            assertThat(content.get(1).getTitle()).isEqualTo("공개1");
        }
    }

    @DisplayName("FindNextPageByIsPublishedTrue 단위테스트")
    @Nested
    class FindNextPageByIsPublishedTrue {

        @DisplayName("커서로 넘긴 displayOrder보다 큰 공개 아티클만 조회한다")
        @Test
        void findNextPageByIsPublishedTrue1() {
            createAndPersist("공개1", 1, true);
            createAndPersist("공개2", 2, true);
            createAndPersist("공개3", 3, true);
            entityManager.flush();

            Slice<Article> result = articleRepository.findNextPageByIsPublishedTrue(1, PageRequest.of(0, 10));

            List<Article> content = result.getContent();
            assertThat(content).hasSize(2);
            assertThat(content.get(0).getTitle()).isEqualTo("공개2");
            assertThat(content.get(1).getTitle()).isEqualTo("공개3");
        }

        @DisplayName("비공개 아티클은 커서 조건을 만족해도 조회되지 않는다")
        @Test
        void findNextPageByIsPublishedTrue2() {
            createAndPersist("공개1", 1, true);
            createAndPersist("비공개", 2, false);
            entityManager.flush();

            Slice<Article> result = articleRepository.findNextPageByIsPublishedTrue(1, PageRequest.of(0, 10));

            assertThat(result.getContent()).isEmpty();
        }
    }
}
