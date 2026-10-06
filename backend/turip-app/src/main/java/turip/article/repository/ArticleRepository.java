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
            LEFT JOIN FETCH a.author
            WHERE (:onlyPublished = false OR a.isPublished = true)
            ORDER BY a.displayOrder ASC
            """)
    Slice<Article> findFirstPage(@Param("onlyPublished") boolean onlyPublished, Pageable pageable);

    @Query("""
            SELECT a FROM Article a
            LEFT JOIN FETCH a.author
            WHERE (:onlyPublished = false OR a.isPublished = true) AND a.displayOrder > :cursorDisplayOrder
            ORDER BY a.displayOrder ASC
            """)
    Slice<Article> findNextPage(@Param("onlyPublished") boolean onlyPublished,
                                @Param("cursorDisplayOrder") int cursorDisplayOrder,
                                Pageable pageable);

    @Query("SELECT MIN(a.displayOrder) FROM Article a")
    Optional<Integer> findMinDisplayOrder();
}
