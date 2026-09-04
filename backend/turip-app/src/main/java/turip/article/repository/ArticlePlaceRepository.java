package turip.article.repository;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.article.domain.ArticlePlace;

public interface ArticlePlaceRepository extends JpaRepository<ArticlePlace, Long> {

    @EntityGraph(attributePaths = {"place"}, type = EntityGraph.EntityGraphType.FETCH)
    List<ArticlePlace> findAllByArticleIdIn(List<Long> articleIds);

    @EntityGraph(attributePaths = {"place"}, type = EntityGraph.EntityGraphType.FETCH)
    List<ArticlePlace> findAllByArticleId(Long articleId);
}
