package turip.favoritefolder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import turip.favoritefolder.domain.FavoriteFolder;

public interface FavoriteFolderRepository extends JpaRepository<FavoriteFolder, Long> {
}
