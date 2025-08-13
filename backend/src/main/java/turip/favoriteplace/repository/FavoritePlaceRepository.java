package turip.favoriteplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import turip.favoriteplace.domain.FavoritePlace;

public interface FavoritePlaceRepository extends JpaRepository<FavoritePlace, Long> {
}
