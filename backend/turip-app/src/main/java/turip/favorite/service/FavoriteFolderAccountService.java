package turip.favorite.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.Account;
import turip.account.domain.Member;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.ForbiddenException;
import turip.common.exception.custom.NotFoundException;
import turip.favorite.domain.AccountRole;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.domain.FavoriteFolderAccount;
import turip.favorite.repository.FavoriteFolderAccountRepository;
import turip.favorite.repository.dto.FavoriteFolderItemCountResult;

@Service
@RequiredArgsConstructor
public class FavoriteFolderAccountService {

    private final FavoriteFolderAccountRepository favoriteFolderAccountRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public FavoriteFolderAccount save(FavoriteFolder favoriteFolder, Account account, AccountRole accountRole) {
        FavoriteFolderAccount favoriteFolderAccount = new FavoriteFolderAccount(favoriteFolder, account, accountRole);
        return favoriteFolderAccountRepository.save(favoriteFolderAccount);
    }

    @Transactional
    public FavoriteFolderAccount findOrCreate(FavoriteFolder favoriteFolder, Account account) {
        return favoriteFolderAccountRepository.findByFavoriteFolderAndAccount(favoriteFolder, account)
                .orElseGet(() -> save(favoriteFolder, account, AccountRole.MEMBER));
    }

    public void validateOwnership(Account account, FavoriteFolder favoriteFolder) {
        boolean isOwner = favoriteFolderAccountRepository.existsByFavoriteFolderAndAccountAndAccountRole(
                favoriteFolder, account, AccountRole.OWNER
        );

        if (!isOwner) {
            throw new ForbiddenException(ErrorTag.FORBIDDEN);
        }
    }

    public void validateMembership(Account account, FavoriteFolder favoriteFolder) {
        if (!isFolderMember(account, favoriteFolder)) {
            throw new ForbiddenException(ErrorTag.FORBIDDEN);
        }
    }

    public boolean isFolderMember(Account account, FavoriteFolder favoriteFolder) {
        return favoriteFolderAccountRepository.existsByFavoriteFolderAndAccount(favoriteFolder, account);
    }

    public List<Member> findMembersByFavoriteFolder(FavoriteFolder favoriteFolder) {
        return favoriteFolderAccountRepository.findMembersByFavoriteFolder(favoriteFolder);
    }

    public int countByFavoriteFolder(FavoriteFolder favoriteFolder) {
        return favoriteFolderAccountRepository.countByFavoriteFolder(favoriteFolder);
    }

    public List<FavoriteFolderItemCountResult> countByFavoriteFolderIdsIn(List<Long> folderIds) {
        return favoriteFolderAccountRepository.countByFavoriteFolderIdsIn(folderIds);
    }

    @Transactional
    public void deleteByFavoriteFolderAndAccount(FavoriteFolder favoriteFolder, Account account) {
        FavoriteFolderAccount favoriteFolderAccount = favoriteFolderAccountRepository.findByFavoriteFolderAndAccount(
                        favoriteFolder, account)
                .orElseThrow(() -> new NotFoundException(ErrorTag.FAVORITE_FOLDER_ACCOUNT_NOT_FOUND));
        favoriteFolderAccountRepository.delete(favoriteFolderAccount);
    }
}
