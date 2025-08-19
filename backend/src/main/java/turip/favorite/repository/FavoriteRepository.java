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
import turip.favorite.domain.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByMemberIdAndContentId(Long memberId, Long contentId);

    Optional<Favorite> findByMemberIdAndContentId(Long memberId, Long contentId);

    @Query(value = """
            SELECT c.*
            FROM favorite f
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

    List<Favorite> findByMemberIdAndContentIdIn(Long memberId, List<Long> contentIds);

    @Query("""
                SELECT f.content
                FROM Favorite f
                JOIN f.member m
                WHERE m.deviceFid = :deviceFid
                  AND f.content.id < :lastContentId
                ORDER BY f.createdAt DESC
            """)
    Slice<Content> findMyFavoriteContentsByDeviceFid(
            @Param("deviceFid") String deviceFid,
            @Param("lastContentId") Long lastContentId,
            Pageable pageable
    );
}
