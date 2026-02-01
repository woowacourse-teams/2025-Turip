package turip.account.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.IllegalArgumentException;
import turip.util.fixture.MemberFixture;
import turip.util.fixture.TuripMemberFixture;

public class TuripMemberTest {

    @DisplayName("아이디 형식이 올바르지 않은 경우 예외를 발생시킨다")
    @ParameterizedTest
    @ValueSource(strings = {"word", "123456789012345678901", "UPPER", "turip~", " turip"})
    void invalidLoginIdTest(String invalidLoginId) {
        // given
        Member member = MemberFixture.createMember();

        // when & then
        assertThatThrownBy(() -> new TuripMember(member, invalidLoginId, "ValidPass1!"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorTag.LOGIN_ID_INVALID.getMessage());

    }

    @DisplayName("비밀번호 형식이 올바르지 않은 경우 예외를 발생시킨다")
    @ParameterizedTest
    @ValueSource(strings = {"word", "123456789012345678901", "ONLY_UPPER1!", "no_number!", "nospecialchar1",
            "turip~~~~", "    turip"})
    void invalidLoginPasswordTest(String invalidLoginPassword) {
        // given
        Member member = MemberFixture.createMember();

        // when & then
        assertThatThrownBy(() -> new TuripMember(member, "turip", invalidLoginPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorTag.LOGIN_PASSWORD_INVALID.getMessage());
    }

    @DisplayName("비밀번호가 매치하는지 확인할 수 있다")
    @ParameterizedTest
    @CsvSource({
            "validPass1!, validPass1!, true",
            "invalidPass1!, validPass1!, false"
    })
    void isPasswordMatch(String inputPassword, String realPassword, boolean expected) {
        // given
        Member member = MemberFixture.createMember();
        TuripMember turipMember = TuripMemberFixture.createCustomTuripMember(member, "turip", realPassword);

        // when & then
        assertThat(turipMember.isPasswordMatch(inputPassword))
                .isEqualTo(expected);
    }
}
