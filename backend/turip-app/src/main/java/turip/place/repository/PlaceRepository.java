package turip.place.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import turip.place.domain.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findByUrl(String url);

    @Query("""
            SELECT DISTINCT cp.place FROM ContentPlace cp
            JOIN cp.content c WHERE c.city.name = :cityName
            """)
    List<Place> findByCityName(@Param("cityName") String cityName, Pageable pageable);
}
