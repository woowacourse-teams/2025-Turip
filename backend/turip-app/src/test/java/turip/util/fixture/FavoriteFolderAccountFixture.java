package turip.util.fixture;

import turip.account.domain.Account;
import turip.favorite.domain.AccountRole;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.domain.FavoriteFolderAccount;

public class FavoriteFolderAccountFixture {

    public static FavoriteFolderAccount createFavoriteFolderAccount() {
        FavoriteFolder defaultFolder = FavoriteFolderFixture.createDefaultFolder();
        Account account = AccountFixture.createUser();
        return new FavoriteFolderAccount(defaultFolder, account, AccountRole.OWNER);
    }

    public static FavoriteFolderAccount createFavoriteFolderAccount(Account account) {
        FavoriteFolder defaultFolder = FavoriteFolderFixture.createDefaultFolder();
        return new FavoriteFolderAccount(defaultFolder, account, AccountRole.OWNER);
    }

    public static FavoriteFolderAccount createFavoriteFolderAccount(FavoriteFolder favoriteFolder, Account account,
                                                                    AccountRole accountRole) {
        return new FavoriteFolderAccount(favoriteFolder, account, accountRole);
    }

    public static FavoriteFolderAccount createFavoriteFolderAccountWithId(Long id, FavoriteFolder favoriteFolder,
                                                                          Account account,
                                                                          AccountRole accountRole) {
        return new FavoriteFolderAccount(id, favoriteFolder, account, accountRole);
    }
}
