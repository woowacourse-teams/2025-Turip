package turip.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import turip.category.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
