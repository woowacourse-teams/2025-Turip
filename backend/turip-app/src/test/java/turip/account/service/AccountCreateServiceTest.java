package turip.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.account.domain.Account;
import turip.account.repository.AccountRepository;
import turip.favorite.service.FavoriteFolderService;
import turip.util.fixture.AccountFixture;

@ExtendWith(MockitoExtension.class)
public class AccountCreateServiceTest {

    @InjectMocks
    private AccountCreateService accountCreateService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private FavoriteFolderService favoriteFolderService;

    @DisplayName("랜덤 닉네임과 Account를 생성하고 기본 찜 폴더를 생성한다")
    @Test
    void create1() {
        // given
        Account savedAccount = AccountFixture.createUser();
        given(accountRepository.save(any()))
                .willReturn(savedAccount);

        // when
        Account result = accountCreateService.save();

        // then
        assertThat(result).isEqualTo(savedAccount);
        verify(favoriteFolderService).createDefaultFavoriteFolder(savedAccount);
    }
}
