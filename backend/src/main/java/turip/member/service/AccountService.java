package turip.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.repository.FavoriteFolderRepository;
import turip.member.domain.Account;
import turip.member.repository.AccountRepository;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final FavoriteFolderRepository favoriteFolderRepository;

    public Account create() {
        Account savedAccount = accountRepository.save(new Account());
        FavoriteFolder defaultFolder = FavoriteFolder.defaultFolderOf(savedAccount);
        favoriteFolderRepository.save(defaultFolder);
        return savedAccount;
    }
}
