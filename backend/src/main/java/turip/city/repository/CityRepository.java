package turip.city.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.city.domain.City;

public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findAllByCountryName(String name);
}
