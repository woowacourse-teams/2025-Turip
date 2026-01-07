package turip.account.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.IllegalArgumentException;
import turip.util.fixture.AccountFixture;

public class MemberTest {

    @DisplayName("이메일 형식이 올바르지 않은 경우 예외를 발생시킨다.")
    @Test
    void invalidEmailTest() {
        // given
        Account account = AccountFixture.createUser();
        String invalidEmail = "invalid-email";

        // when & then
        assertThatThrownBy(() -> new Member(account, invalidEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorTag.EMAIL_INVALID.getMessage());
    }
}
