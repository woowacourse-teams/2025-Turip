package turip.favorite.service;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.account.domain.Account;
import turip.favorite.domain.AccountRole;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.domain.FavoriteFolderAccount;
import turip.favorite.repository.FavoriteFolderAccountRepository;
import turip.favorite.repository.FavoriteFolderRepository;
import turip.util.fixture.AccountFixture;
import turip.util.fixture.FavoriteFolderAccountFixture;
import turip.util.fixture.FavoriteFolderFixture;

@ExtendWith(MockitoExtension.class)
public class FavoriteFolderAccountTest {

    @InjectMocks
    FavoriteFolderAccountService favoriteFolderAccountService;

    @Mock
    private FavoriteFolderAccountRepository favoriteFolderAccountRepository;

    @Mock
    private FavoriteFolderRepository favoriteFolderRepository;

    @DisplayName("removeByAccount() 테스트")
    @Nested
    class RemoveByAccountTest {

        @DisplayName("계정의 폴더 삭제 시, 공유 폴더는 유지시킨다")
        @Test
        void removeByAccountTest1() {
            // given
            Account account = AccountFixture.createUser();
            FavoriteFolder customFolder = FavoriteFolderFixture.createCustomFolderWithId(1L, "개인 폴더");
            FavoriteFolder sharedFolder = FavoriteFolderFixture.createSharedFolderWithId(2L, "공유 폴더");
            FavoriteFolderAccount customFavoriteFolderAccount = FavoriteFolderAccountFixture.createFavoriteFolderAccount(
                    customFolder, account, AccountRole.OWNER);
            FavoriteFolderAccount sharedFavoriteFolderAccount = FavoriteFolderAccountFixture.createFavoriteFolderAccount(
                    sharedFolder, account, AccountRole.OWNER);
            given(favoriteFolderAccountRepository.findAllByAccount(account))
                    .willReturn(List.of(customFavoriteFolderAccount, sharedFavoriteFolderAccount));

            // when
            favoriteFolderAccountService.removeByAccount(account);

            // then
            verify(favoriteFolderRepository, times(1)).delete(customFolder);
            verify(favoriteFolderRepository, times(0)).delete(sharedFolder);

        }
    }
}
