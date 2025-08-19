package turip.favorite.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.favorite.domain.FavoriteFolder;
import turip.member.domain.Member;

public interface FavoriteFolderRepository extends JpaRepository<FavoriteFolder, Long> {

    boolean existsByNameAndMember(String name, Member member);

    List<FavoriteFolder> findAllByMember(Member member);
}
