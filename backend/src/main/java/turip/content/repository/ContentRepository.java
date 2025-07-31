package turip.content.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import turip.content.domain.Content;

public interface ContentRepository extends JpaRepository<Content, Long> {

    int countByRegion_Name(String regionName);

    List<Content> findByRegionNameAndIdLessThanOrderByIdDesc(String regionName, Long lastId, Pageable pageable);

    List<Content> findByRegionNameOrderByIdDesc(String regionName, Pageable pageable);

    @Query("""
                SELECT c FROM Content c
                JOIN c.creator cr
                WHERE c.id < :lastId
                AND (c.title LIKE %:keyword% OR cr.channelName LIKE %:keyword%)
                ORDER BY c.id DESC
            """)
    Slice<Content> findByKeyword(String keyword, Long lastId, Pageable pageable);
}
