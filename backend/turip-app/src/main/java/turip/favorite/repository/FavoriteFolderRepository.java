package turip.favorite.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.account.domain.Account;
import turip.favorite.domain.FavoriteFolder;

public interface FavoriteFolderRepository extends JpaRepository<FavoriteFolder, Long> {

    boolean existsByNameAndAccount(String name, Account account);

    List<FavoriteFolder> findAllByAccountOrderByIdAsc(Account account);

    List<FavoriteFolder> findAllByAccount(Account account);

    boolean existsByAccountAndIsDefault(Account account, boolean isDefault);

    void deleteByAccountAndIsDefault(Account account, boolean isDefault);
}
