package turip.favoritefolder.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.favoritefolder.domain.FavoriteFolder;

public interface FavoriteFolderRepository extends JpaRepository<FavoriteFolder, Long> {
    
    List<FavoriteFolder> findByMemberId(Long memberId);
    
    List<FavoriteFolder> findByMemberIdAndIsDefaultTrue(Long memberId);
}
