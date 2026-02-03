package turip.account.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.account.domain.Account;
import turip.account.repository.MemberRepository;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.IllegalArgumentException;
import turip.util.fixture.AccountFixture;

@ExtendWith(MockitoExtension.class)
public class MemberCreateServiceTest {

    @InjectMocks
    private MemberCreateService memberCreateService;

    @Mock
    private MemberRepository memberRepository;

    @DisplayName("이메일 형식이 올바르지 않은 경우 예외를 발생시킨다.")
    @Test
    void save1() {
        // given
        String invalidEmail = "invalid-email";
        Account account = AccountFixture.createUser();

        // when & then
        assertThatThrownBy(() -> memberCreateService.save(account, invalidEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorTag.EMAIL_INVALID.getMessage());
    }
}
