package turip.favorite.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.account.domain.Account;
import turip.favorite.domain.AccountRole;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.domain.FavoriteFolderAccount;

public interface FavoriteFolderAccountRepository extends JpaRepository<FavoriteFolderAccount, Long> {

    List<FavoriteFolderAccount> findAllByAccount(Account account);

    boolean existsByFavoriteFolderAndAccountAndAccountRole(FavoriteFolder favoriteFolder, Account account,
                                                           AccountRole accountRole);
}
