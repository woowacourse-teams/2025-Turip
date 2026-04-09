package turip.favorite.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.account.domain.Account;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.ForbiddenException;
import turip.common.exception.custom.NotFoundException;
import turip.favorite.domain.AccountRole;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.domain.FavoriteFolderAccount;
import turip.favorite.repository.FavoriteFolderAccountRepository;
import turip.util.fixture.AccountFixture;
import turip.util.fixture.FavoriteFolderAccountFixture;
import turip.util.fixture.FavoriteFolderFixture;

@ExtendWith(MockitoExtension.class)
class FavoriteFolderAccountServiceTest {

    @InjectMocks
    private FavoriteFolderAccountService favoriteFolderAccountService;

    @Mock
    private FavoriteFolderAccountRepository favoriteFolderAccountRepository;

    @DisplayName("폴더에 대한 OWNER인지 확인한다")
    @Test
    void validateOwnership1() {
        // given
        Account account = AccountFixture.createUser();
        FavoriteFolder folder = FavoriteFolderFixture.createCustomFolder("옮길 폴더");

        given(favoriteFolderAccountRepository.existsByFavoriteFolderAndAccountAndAccountRole(folder, account,
                AccountRole.OWNER))
                .willReturn(true);

        // when & then
        assertDoesNotThrow(() -> favoriteFolderAccountService.validateOwnership(account, folder));
    }

    @DisplayName("폴더에 대한 OWNER가 아닌 경우 예외가 발생한다")
    @Test
    void validateOwnership2() {
        // given
        Account account = AccountFixture.createUser();
        FavoriteFolder folder = FavoriteFolderFixture.createCustomFolder("옮길 폴더");

        given(favoriteFolderAccountRepository.existsByFavoriteFolderAndAccountAndAccountRole(folder, account,
                AccountRole.OWNER))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> favoriteFolderAccountService.validateOwnership(account, folder))
                .isInstanceOf(ForbiddenException.class);
    }

    @DisplayName("폴더에 대한 OWNER 혹은 MEMBER인지 확인한다")
    @Test
    void validateMembership1() {
        // given
        Account account = AccountFixture.createUser();
        FavoriteFolder folder = FavoriteFolderFixture.createCustomFolder("옮길 폴더");

        given(favoriteFolderAccountRepository.existsByFavoriteFolderIdAndAccount(folder.getId(), account))
                .willReturn(true);

        // when & then
        assertDoesNotThrow(() -> favoriteFolderAccountService.validateMembership(account, folder));
    }

    @DisplayName("폴더에 대한 OWNER 혹은 MEMBER가 아닌 경우 예외가 발생한다")
    @Test
    void validateMembership2() {
        // given
        Account account = AccountFixture.createUser();
        FavoriteFolder folder = FavoriteFolderFixture.createCustomFolder("옮길 폴더");

        given(favoriteFolderAccountRepository.existsByFavoriteFolderIdAndAccount(folder.getId(), account))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> favoriteFolderAccountService.validateMembership(account, folder))
                .isInstanceOf(ForbiddenException.class);
    }

    @DisplayName("FavoriteFolderAccount의 Account를 업데이트한다")
    @Test
    void updateAccount() {
        // given
        Account oldAccount = AccountFixture.createUser();
        Account newAccount = AccountFixture.createUser();

        FavoriteFolder folder = FavoriteFolderFixture.createCustomFolder("옮길 폴더");
        FavoriteFolderAccount favoriteFolderAccount = FavoriteFolderAccountFixture.createFavoriteFolderAccount(folder,
                oldAccount, AccountRole.OWNER);
        given(favoriteFolderAccountRepository.findByAccount(oldAccount))
                .willReturn(List.of(favoriteFolderAccount));

        // when
        favoriteFolderAccountService.updateAccount(oldAccount, newAccount);

        // then
        assertThat(favoriteFolderAccount.getAccount())
                .isEqualTo(newAccount);
    }

    @DisplayName("account와 folder에 대한 FavoriteFolderAccount를 삭제한다")
    @Test
    void deleteByFavoriteFolderAndAccount1() {
        // given
        Account account = AccountFixture.createUser();

        FavoriteFolder folder = FavoriteFolderFixture.createCustomFolder("옮길 폴더");
        FavoriteFolderAccount favoriteFolderAccount = FavoriteFolderAccountFixture.createFavoriteFolderAccount(folder,
                account, AccountRole.OWNER);
        given(favoriteFolderAccountRepository.findByFavoriteFolderAndAccount(folder, account))
                .willReturn(Optional.of(favoriteFolderAccount));

        // when
        favoriteFolderAccountService.deleteByFavoriteFolderAndAccount(folder, account);

        // then
        verify(favoriteFolderAccountRepository, times(1)).delete(favoriteFolderAccount);
    }

    @DisplayName("account와 folder에 대한 FavoriteFolderAccount를 찾을 수 없는 경우 예외가 발생한다")
    @Test
    void deleteByFavoriteFolderAndAccount2() {
        // given
        Account account = AccountFixture.createUser();

        FavoriteFolder folder = FavoriteFolderFixture.createCustomFolder("옮길 폴더");
        given(favoriteFolderAccountRepository.findByFavoriteFolderAndAccount(folder, account))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> favoriteFolderAccountService.deleteByFavoriteFolderAndAccount(folder, account))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(ErrorTag.FAVORITE_FOLDER_ACCOUNT_NOT_FOUND.getMessage());
    }
}
