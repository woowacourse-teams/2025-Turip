package turip.content.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import turip.content.domain.Content;

public interface ContentRepository extends JpaRepository<Content, Long> {

    int countByCityName(String provinceName);

    List<Content> findByCityNameAndIdLessThanOrderByIdDesc(String provinceName, Long lastId, Pageable pageable);

    List<Content> findByCityNameOrderByIdDesc(String provinceName, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Content c JOIN c.city.country co WHERE co.name = :countryName")
    int countByCountryName(@Param("countryName") String countryName);

    int countByCityNameNotIn(List<String> cityNames);

    @Query("SELECT COUNT(c) FROM Content c JOIN c.city.country co WHERE co.name NOT IN :countryNames")
    int countByCountryNameNotIn(@Param("countryNames") List<String> countryNames);
}
