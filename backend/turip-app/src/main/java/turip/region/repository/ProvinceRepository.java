package turip.region.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.region.domain.Province;

public interface ProvinceRepository extends JpaRepository<Province, Long> {

    Optional<Province> findByName(String name);
} 
