package turip.favorite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import turip.account.domain.Account;
import turip.favorite.domain.AccountRole;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.domain.FavoriteFolderAccount;

public interface FavoriteFolderAccountRepository extends JpaRepository<FavoriteFolderAccount, Long> {

    boolean existsByFavoriteFolderAndAccountAndAccountRole(FavoriteFolder favoriteFolder, Account account,
                                                           AccountRole accountRole);

    @Modifying
    @Query("UPDATE FavoriteFolderAccount ffa " +
            "SET ffa.account = :newAccount " +
            "WHERE ffa.account = :oldAccount")
    void updateAccount(@Param("oldAccount") Account oldAccount, @Param("newAccount") Account newAccount);
}
