package turip.favorite.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.Account;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.ForbiddenException;
import turip.common.exception.custom.NotFoundException;
import turip.favorite.domain.AccountRole;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.domain.FavoriteFolderAccount;
import turip.favorite.repository.FavoriteFolderAccountRepository;
import turip.favorite.repository.FavoriteFolderRepository;

@Service
@RequiredArgsConstructor
// 폴더-사용자 관계 관리
public class FavoriteFolderAccountService {

    private final FavoriteFolderAccountRepository favoriteFolderAccountRepository;
    private final FavoriteFolderRepository favoriteFolderRepository;

    @Transactional
    public void save(FavoriteFolder favoriteFolder, Account account, AccountRole accountRole) {
        FavoriteFolderAccount favoriteFolderAccount = new FavoriteFolderAccount(favoriteFolder, account, accountRole);
        favoriteFolderAccountRepository.save(favoriteFolderAccount);
    }

    public List<FavoriteFolderAccount> findAllByAccount(Account account) {
        return favoriteFolderAccountRepository.findAllByAccount(account);
    }

    public List<FavoriteFolderAccount> findAllByAccountOrderByIdAsc(Account account) {
        return favoriteFolderAccountRepository.findAllByAccountOrderByIdAsc(account);
    }

    @Transactional
    public void removeByAccount(Account account) {
        favoriteFolderAccountRepository.findAllByAccount(account).
                forEach(this::removeWithFavoriteFolder);
    }

    public void validateOwnership(Account account, FavoriteFolder favoriteFolder) {
        FavoriteFolderAccount favoriteFolderAccount = favoriteFolderAccountRepository.findByAccountAndFavoriteFolder(
                        account, favoriteFolder)
                .orElseThrow(() -> new NotFoundException(ErrorTag.FAVORITE_FOLDER_NOT_FOUND));

        if (!favoriteFolderAccount.isRoleOf(AccountRole.OWNER)) {
            throw new ForbiddenException(ErrorTag.FORBIDDEN);
        }
    }

    private void removeWithFavoriteFolder(FavoriteFolderAccount favoriteFolderAccount) {
        favoriteFolderRepository.delete(favoriteFolderAccount.getFavoriteFolder());
        favoriteFolderAccountRepository.delete(favoriteFolderAccount);
    }
}
