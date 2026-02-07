package turip.favorite.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.Account;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.ForbiddenException;
import turip.favorite.domain.AccountRole;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.domain.FavoriteFolderAccount;
import turip.favorite.repository.FavoriteFolderAccountRepository;

@Service
@RequiredArgsConstructor
public class FavoriteFolderAccountService {

    private final FavoriteFolderAccountRepository favoriteFolderAccountRepository;

    @Transactional
    public void save(FavoriteFolder favoriteFolder, Account account, AccountRole accountRole) {
        FavoriteFolderAccount favoriteFolderAccount = new FavoriteFolderAccount(favoriteFolder, account, accountRole);
        favoriteFolderAccountRepository.save(favoriteFolderAccount);
    }

    public void validateOwnership(Account account, FavoriteFolder favoriteFolder) {
        boolean isOwner = favoriteFolderAccountRepository.existsByFavoriteFolderAndAccountAndAccountRole(
                favoriteFolder, account, AccountRole.OWNER
        );

        if (!isOwner) {
            throw new ForbiddenException(ErrorTag.FORBIDDEN);
        }
    }
}
