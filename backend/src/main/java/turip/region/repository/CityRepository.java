package turip.region.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.region.domain.City;

public interface CityRepository extends JpaRepository<City, Long> {

    @EntityGraph(attributePaths = {"country"}, type = EntityGraph.EntityGraphType.FETCH)
    List<City> findAllByCountryName(String name);

    Optional<City> findByName(String name);
}
