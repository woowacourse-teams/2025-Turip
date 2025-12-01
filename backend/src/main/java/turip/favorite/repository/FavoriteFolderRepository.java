package turip.favorite.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.favorite.domain.FavoriteFolder;
import turip.member.domain.Account;

public interface FavoriteFolderRepository extends JpaRepository<FavoriteFolder, Long> {

    boolean existsByNameAndAccount(String name, Account account);

    List<FavoriteFolder> findAllByAccountOrderByIdAsc(Account account);

    List<FavoriteFolder> findAllByAccount(Account account);

    void deleteByAccountAndIsDefault(Account account, boolean isDefault);
}
