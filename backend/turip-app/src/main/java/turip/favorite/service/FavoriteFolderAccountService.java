package turip.favorite.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.Account;
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
}
