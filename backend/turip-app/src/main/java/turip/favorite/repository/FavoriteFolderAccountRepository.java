package turip.favorite.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.account.domain.Account;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.domain.FavoriteFolderAccount;

public interface FavoriteFolderAccountRepository extends JpaRepository<FavoriteFolderAccount, Long> {

    List<FavoriteFolderAccount> findAllByAccount(Account account);

    List<FavoriteFolderAccount> findAllByAccountOrderByIdAsc(Account account);

    Optional<FavoriteFolderAccount> findByAccountAndFavoriteFolder(Account account, FavoriteFolder favoriteFolder);
}
