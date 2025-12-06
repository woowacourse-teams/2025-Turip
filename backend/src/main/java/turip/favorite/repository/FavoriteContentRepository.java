package turip.favorite.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import turip.content.domain.Content;
import turip.favorite.domain.FavoriteContent;
import turip.account.domain.Account;

public interface FavoriteContentRepository extends JpaRepository<FavoriteContent, Long> {

    boolean existsByAccountIdAndContentId(Long accountId, Long contentId);

    Optional<FavoriteContent> findByAccountIdAndContentId(Long accountId, Long contentId);


    @Query(value = """
            SELECT c.*
            FROM favorite_content f
            JOIN content c ON f.content_id = c.id
            WHERE f.created_at BETWEEN :startDate AND :endDate
            GROUP BY f.content_id
            ORDER BY COUNT(*) DESC
            LIMIT :topContentSize
            """, nativeQuery = true)
    List<Content> findPopularContentsByFavoriteBetweenDatesWithLimit(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("topContentSize") int topContentSize
    );

    List<FavoriteContent> findByAccountIdAndContentIdIn(Long accountId, List<Long> contentIds);

    @Query("""
                SELECT f.content
                FROM FavoriteContent f
                JOIN f.account a
                JOIN FETCH f.content.creator
                JOIN FETCH f.content.city
                WHERE a.id = :accountId
                  AND f.content.id < :lastContentId
                ORDER BY f.createdAt DESC
            """)
    Slice<Content> findMyFavoriteContentsByAccountId(
            @Param("accountId") Long accountId,
            @Param("lastContentId") Long lastContentId,
            Pageable pageable
    );

    List<FavoriteContent> findAllByAccount(Account account);

    void deleteByAccount(Account account);
}
