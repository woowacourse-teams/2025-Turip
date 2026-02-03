package turip.account.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import turip.account.domain.Account;
import turip.account.domain.Member;
import turip.account.repository.MemberRepository;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.IllegalArgumentException;
import turip.util.fixture.AccountFixture;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MemberCreateServiceTest.RetryTestConfig.class)
public class MemberCreateServiceTest {

    @Autowired
    private MemberCreateService memberCreateService;

    @Autowired
    private MemberRepository memberRepository;

    @Configuration
    @EnableRetry
    static class RetryTestConfig {

        @Bean
        MemberRepository memberRepository() {
            return mock(MemberRepository.class);
        }

        @Bean
        MemberCreateService memberCreateService(MemberRepository memberRepository) {
            return new MemberCreateService(memberRepository);
        }
    }

    @BeforeEach
    void setUp() {
        reset(memberRepository);
    }

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

    @DisplayName("랜덤 닉네임이 중복되는 경우 4번까지 재시도하고 5번째 성공 시 저장된다.")
    @Test
    void save2() {
        // given
        Account account = AccountFixture.createUser();
        String email = "test@example.com";
        Member savedMember = new Member(account, email, "테스트 닉네임", true);
        when(memberRepository.save(any(Member.class)))
                .thenThrow(new DuplicateKeyException("dup1"))
                .thenThrow(new DuplicateKeyException("dup2"))
                .thenThrow(new DuplicateKeyException("dup3"))
                .thenThrow(new DuplicateKeyException("dup4"))
                .thenReturn(savedMember);

        // when & then
        assertThatCode(() -> memberCreateService.save(account, email))
                .doesNotThrowAnyException();
        verify(memberRepository, times(5)).save(any(Member.class));
    }

    @DisplayName("중복 예외가 5번 연속 발생하면 예외를 던진다.")
    @Test
    void save3() {
        // given
        Account account = AccountFixture.createUser();
        String email = "test@example.com";
        when(memberRepository.save(any(Member.class)))
                .thenThrow(new DuplicateKeyException("dup1"))
                .thenThrow(new DuplicateKeyException("dup2"))
                .thenThrow(new DuplicateKeyException("dup3"))
                .thenThrow(new DuplicateKeyException("dup4"))
                .thenThrow(new DuplicateKeyException("dup5"));

        // when & then
        assertThatThrownBy(() -> memberCreateService.save(account, email))
                .isInstanceOf(DuplicateKeyException.class);
        verify(memberRepository, times(5)).save(any(Member.class));
    }
}
