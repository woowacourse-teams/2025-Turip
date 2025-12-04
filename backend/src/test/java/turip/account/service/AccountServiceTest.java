package turip.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.NotFoundException;
import turip.favorite.repository.FavoriteContentRepository;
import turip.favorite.service.FavoriteFolderService;
import turip.account.domain.Account;
import turip.account.repository.AccountRepository;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private FavoriteContentRepository favoriteContentRepository;

    @Mock
    private FavoriteFolderService favoriteFolderService;

    @DisplayName("Account 생성 테스트")
    @Nested
    class Create {

        @DisplayName("Account를 생성하고 기본 찜 폴더를 생성한다")
        @Test
        void create() {
            // given
            Account savedAccount = new Account(1L);
            given(accountRepository.save(any(Account.class)))
                    .willReturn(savedAccount);

            // when
            Account result = accountService.create();

            // then
            assertThat(result).isEqualTo(savedAccount);
            verify(favoriteFolderService).createDefaultFavoriteFolder(savedAccount);
        }
    }

    @DisplayName("Account 조회 테스트")
    @Nested
    class GetById {

        @DisplayName("ID로 Account를 조회할 수 있다")
        @Test
        void getById1() {
            // given
            Long accountId = 1L;
            Account account = new Account(accountId);
            given(accountRepository.findById(accountId))
                    .willReturn(Optional.of(account));

            // when
            Account result = accountService.getById(accountId);

            // then
            assertThat(result).isEqualTo(account);
        }

        @DisplayName("존재하지 않는 Account ID로 조회 시 NotFoundException을 발생시킨다")
        @Test
        void getById2() {
            // given
            Long nonExistentAccountId = 999L;
            given(accountRepository.findById(nonExistentAccountId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> accountService.getById(nonExistentAccountId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(ErrorTag.ACCOUNT_NOT_FOUND.getMessage());
        }
    }

    @DisplayName("Account 및 관련 찜 데이터 삭제 테스트")
    @Nested
    class DeleteAccountAndFavorites {

        @DisplayName("Account와 관련된 모든 찜 콘텐츠, 찜 폴더를 삭제한다")
        @Test
        void deleteAccountAndFavorites() {
            // given
            Long accountId = 1L;
            Account account = new Account(accountId);

            // when
            accountService.deleteAccountAndFavorites(account);

            // then
            verify(favoriteContentRepository).deleteByAccount(account);
            verify(favoriteFolderService).removeByAccount(account);
            verify(accountRepository).delete(account);
        }
    }
}
