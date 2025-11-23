package turip.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.NotFoundException;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.repository.FavoriteFolderRepository;
import turip.member.domain.Account;
import turip.member.repository.AccountRepository;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final FavoriteFolderRepository favoriteFolderRepository;

    @Transactional
    public Account create() {
        Account savedAccount = accountRepository.save(new Account());
        FavoriteFolder defaultFolder = FavoriteFolder.defaultFolderOf(savedAccount);
        favoriteFolderRepository.save(defaultFolder);
        return savedAccount;
    }

    public Account getById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.ACCOUNT_NOT_FOUND));
    }
}
