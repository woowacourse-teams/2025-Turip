package turip.place.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.place.domain.Category;
import turip.place.domain.Place;
import turip.place.domain.PlaceCategory;

public interface PlaceCategoryRepository extends JpaRepository<PlaceCategory, Long> {

    Optional<PlaceCategory> findByPlaceAndCategory(Place place, Category category);
}
