package turip.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.account.controller.dto.request.TuripMemberRequest;
import turip.account.controller.dto.response.TuripMemberResponse;
import turip.account.domain.Member;
import turip.account.domain.Role;
import turip.account.domain.TuripMember;
import turip.account.repository.TuripMemberRepository;
import turip.auth.controller.dto.request.TuripLoginRequest;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.common.exception.custom.ConflictException;
import turip.common.exception.custom.UnauthorizedException;
import turip.util.fixture.AccountFixture;
import turip.util.fixture.MemberFixture;

@ExtendWith(MockitoExtension.class)
class TuripMemberServiceTest {

    @InjectMocks
    private TuripMemberService turipMemberService;

    @Mock
    private TuripMemberRepository turipMemberRepository;

    @Mock
    private MemberService memberService;

    @DisplayName("TuripMember 생성 테스트")
    @Nested
    class CreateTest {

        @DisplayName("TuripMember를 생성할 수 있다")
        @Test
        void create1() {
            // given
            TuripMemberRequest request = new TuripMemberRequest("valid_user-123", "ValidPass1!", "test@test.com");

            Member member = MemberFixture.createCustomMember(AccountFixture.createCustomAccount(1L, Role.USER),
                    "test@test.com", true);
            TuripMember turipMember = new TuripMember(member, request.loginId(), request.loginPassword());

            given(memberService.create(request.email())).willReturn(member);
            given(turipMemberRepository.save(any(TuripMember.class))).willReturn(turipMember);

            // when
            TuripMemberResponse response = turipMemberService.create(request);

            // then
            assertThat(response).isNotNull();
        }

        @DisplayName("아이디 형식이 올바르지 않은 경우 예외를 발생시킨다")
        @ParameterizedTest
        @ValueSource(strings = {"word", "123456789012345678901", "UPPER", "turip~", " turip"})
        void create2(String invalidLoginId) {
            // given
            TuripMemberRequest request = new TuripMemberRequest(invalidLoginId, "ValidPass1!", "test@test.com");

            Member member = MemberFixture.createCustomMember(AccountFixture.createCustomAccount(1L, Role.USER),
                    "test@test.com", true);
            given(memberService.create(request.email())).willReturn(member);

            // when & then
            assertThatThrownBy(() -> turipMemberService.create(request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(ErrorTag.LOGIN_ID_INVALID.getMessage());

        }

        @DisplayName("비밀번호 형식이 올바르지 않은 경우 예외를 발생시킨다")
        @ParameterizedTest
        @ValueSource(strings = {"word", "123456789012345678901", "ONLY_UPPER1!", "no_number!", "nospecialchar1",
                "turip~~~~", "    turip"})
        void create3(String invalidLoginPassword) {
            // given
            TuripMemberRequest request = new TuripMemberRequest("turip", invalidLoginPassword, "test@test.com");

            Member member = MemberFixture.createCustomMember(AccountFixture.createCustomAccount(1L, Role.USER),
                    "test@test.com", true);
            given(memberService.create(request.email())).willReturn(member);

            // when & then
            assertThatThrownBy(() -> turipMemberService.create(request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(ErrorTag.LOGIN_PASSWORD_INVALID.getMessage());

        }
    }

    @DisplayName("중복 아이디가 존재하는 경우 예외를 발생시킨다.")
    @Test
    void create4() {
        // given
        String loginId = "turip";
        TuripMemberRequest request = new TuripMemberRequest(loginId, "ValidPass1!", "test@test.com");
        given(turipMemberRepository.existsByLoginId(loginId))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> turipMemberService.create(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage(ErrorTag.LOGIN_ID_CONFLICT.getMessage());
    }

    @DisplayName("자체 로그인 기능 테스트")
    @Nested
    class Login {

        @DisplayName("로그인을 시도할 수 있다")
        @Test
        void login1() {
            // given
            String loginId = "turip";
            String loginPassword = "ValidPass1!";

            TuripLoginRequest request = new TuripLoginRequest(loginId, loginPassword);
            Member member = MemberFixture.createCustomMember(AccountFixture.createCustomAccount(1L, Role.USER),
                    "test@test.com", true);
            TuripMember turipMember = new TuripMember(member, loginId, loginPassword);

            given(turipMemberRepository.findByLoginId(loginId)).willReturn(Optional.of(turipMember));

            // when
            TuripMember loginTuripMember = turipMemberService.login(request);

            // then
            assertThat(loginTuripMember.getLoginId())
                    .isEqualTo(loginId);
        }

        @DisplayName("로그인을 시도하는 아이디가 존재하지 않는 경우 예외를 발생시킨다")
        @Test
        void login2() {
            // given
            String loginId = "turip";
            String loginPassword = "ValidPass1!";

            TuripLoginRequest request = new TuripLoginRequest(loginId, loginPassword);
            Member member = MemberFixture.createCustomMember(AccountFixture.createCustomAccount(1L, Role.USER),
                    "test@test.com", true);
            TuripMember turipMember = new TuripMember(member, loginId, loginPassword);

            given(turipMemberRepository.findByLoginId(loginId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> turipMemberService.login(request))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage(ErrorTag.CREDENTIALS_INVALID.getMessage());
        }

        @DisplayName("비밀번호가 일치하지 않는 경우 예외를 발생시킨다")
        @Test
        void login3() {
            // given
            String loginId = "turip";
            String loginPassword = "invalidPass1!";
            String realPassword = "validPass1!";

            TuripLoginRequest request = new TuripLoginRequest(loginId, loginPassword);
            Member member = MemberFixture.createCustomMember(AccountFixture.createCustomAccount(1L, Role.USER),
                    "test@test.com", true);
            TuripMember turipMember = new TuripMember(member, loginId, realPassword);

            given(turipMemberRepository.findByLoginId(loginId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> turipMemberService.login(request))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage(ErrorTag.CREDENTIALS_INVALID.getMessage());
        }
    }
}
