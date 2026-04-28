package turip.content.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import turip.account.domain.Account;
import turip.content.domain.ContentPending;
import turip.content.domain.ContentPendingStatus;

public interface ContentPendingRepository extends JpaRepository<ContentPending, Long> {

    Optional<ContentPending> findTopByOrderByIdDesc();

    @Query(value = "SELECT COUNT(*) FROM content_pending " +
            "WHERE JSON_UNQUOTE(JSON_EXTRACT(content_data, '$.video.url')) = :url " +
            "AND status = :status", nativeQuery = true)
    long countByVideoUrlAndStatus(@Param("url") String url, @Param("status") String status);

    long countByStatus(ContentPendingStatus status);

    List<ContentPending> findAllByStatusOrderByIdDesc(ContentPendingStatus status);

    List<ContentPending> findAllByCollectorAccountOrderByIdDesc(Account collectorAccount);

    default boolean existsByVideoUrlAndStatus(String url, ContentPendingStatus status) {
        return countByVideoUrlAndStatus(url, status.name()) > 0;
    }
}
