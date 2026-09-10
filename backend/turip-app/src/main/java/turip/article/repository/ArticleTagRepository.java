package turip.article.repository;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.article.domain.ArticleTag;

public interface ArticleTagRepository extends JpaRepository<ArticleTag, Long> {

    @EntityGraph(attributePaths = {"tag"}, type = EntityGraph.EntityGraphType.FETCH)
    List<ArticleTag> findAllByArticleIdIn(List<Long> articleIds);

    @EntityGraph(attributePaths = {"tag"}, type = EntityGraph.EntityGraphType.FETCH)
    List<ArticleTag> findAllByArticleId(Long articleId);
}
