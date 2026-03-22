package turip.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import turip.content.domain.ContentPending;

public interface ContentPendingRepository extends JpaRepository<ContentPending, Long> {

}