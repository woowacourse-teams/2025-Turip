package turip.content.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.content.domain.Content;

public interface ContentRepository extends JpaRepository<Content, Long> {

    int countByRegion_Name(String regionName);

    List<Content> findByRegion_NameAndIdLessThanOrderByIdDesc(String regionName, Long lastId, Pageable pageable);

    List<Content> findByRegion_NameOrderByIdDesc(String regionName, Pageable pageable);
}
