package turip.place.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.place.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);
}
