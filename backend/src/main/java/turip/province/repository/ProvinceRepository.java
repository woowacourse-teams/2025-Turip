package turip.province.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import turip.province.domain.Province;

import java.util.Optional;

public interface ProvinceRepository extends JpaRepository<Province, Long> {
    Optional<Province> findByName(String name);
} 