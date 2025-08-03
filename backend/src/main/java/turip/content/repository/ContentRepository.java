package turip.content.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.content.domain.Content;

public interface ContentRepository extends JpaRepository<Content, Long> {

    int countByCityName(String provinceName);

    Slice<Content> findByCityNameAndIdLessThanOrderByIdDesc(String provinceName, Long lastId, Pageable pageable);

    Slice<Content> findByCityNameOrderByIdDesc(String provinceName, Pageable pageable);
}
