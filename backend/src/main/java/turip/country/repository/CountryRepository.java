package turip.country.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.country.domain.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {

    List<Country> findAllByNameNot(String name);
}
