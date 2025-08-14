package turip.favoritefolder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import turip.favoritefolder.domain.FavoriteFolder;
import turip.member.domain.Member;

public interface FavoriteFolderRepository extends JpaRepository<FavoriteFolder, Long> {

    boolean existsByNameAndMember(String name, Member member);
}
