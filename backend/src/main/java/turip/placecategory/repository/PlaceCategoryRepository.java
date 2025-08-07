package turip.placecategory.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.placecategory.domain.PlaceCategory;
import turip.category.domain.Category;
import turip.place.domain.Place;

public interface PlaceCategoryRepository extends JpaRepository<PlaceCategory, Long> {
    Optional<PlaceCategory> findByPlaceAndCategory(Place place, Category category);
}
