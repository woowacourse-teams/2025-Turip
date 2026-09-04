package turip.article.repository;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import turip.article.domain.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    Optional<Article> findByIdAndIsPublishedTrue(Long id);

    @Query("""
            SELECT a FROM Article a
            WHERE a.isPublished = true
            ORDER BY a.displayOrder ASC
            """)
    Slice<Article> findFirstPageByIsPublishedTrue(Pageable pageable);

    @Query("""
            SELECT a FROM Article a
            WHERE a.isPublished = true AND a.displayOrder > :cursorDisplayOrder
            ORDER BY a.displayOrder ASC
            """)
    Slice<Article> findNextPageByIsPublishedTrue(@Param("cursorDisplayOrder") int cursorDisplayOrder,
                                                 Pageable pageable);
}
