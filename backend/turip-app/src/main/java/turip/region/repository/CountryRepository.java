package turip.region.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.region.domain.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {

    List<Country> findAllByNameNot(String name);

    Optional<Country> findByName(String name);
}
