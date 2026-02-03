package turip.favorite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import turip.favorite.domain.FavoriteFolder;

public interface FavoriteFolderRepository extends JpaRepository<FavoriteFolder, Long> {
}
