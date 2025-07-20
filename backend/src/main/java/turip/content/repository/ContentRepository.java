package turip.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import turip.content.domain.Content;

public interface ContentRepository extends JpaRepository<Content, Long> {
}
