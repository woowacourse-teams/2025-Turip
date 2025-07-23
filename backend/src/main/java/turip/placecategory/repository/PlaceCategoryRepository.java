package turip.placecategory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import turip.placecategory.domain.PlaceCategory;

public interface PlaceCategoryRepository extends JpaRepository<PlaceCategory, Long> {
}
