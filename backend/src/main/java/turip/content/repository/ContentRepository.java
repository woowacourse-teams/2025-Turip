package turip.content.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.content.domain.Content;

public interface ContentRepository extends JpaRepository<Content, Long> {

    int countByCityName(String provinceName);

    List<Content> findByCityNameAndIdLessThanOrderByIdDesc(String provinceName, Long lastId, Pageable pageable);

    List<Content> findByCityNameOrderByIdDesc(String provinceName, Pageable pageable);
}
