package turip.account.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import turip.account.repository.AccountRepository;
import turip.common.exception.custom.IllegalArgumentException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = NicknameCreateServiceTest.RetryTestConfig.class)
public class NicknameCreateServiceTest {

    @Autowired
    private NicknameCreateService nicknameCreateService;

    @Autowired
    private AccountRepository accountRepository;

    @Configuration
    @EnableRetry
    static class RetryTestConfig {

        @Bean
        AccountRepository accountRepository() {
            return mock(AccountRepository.class);
        }

        @Bean
        NicknameCreateService accountCreateService(AccountRepository accountRepository) {
            return new NicknameCreateService(accountRepository);
        }
    }

    @BeforeEach
    void setUp() {
        reset(accountRepository);
    }

    @DisplayName("랜덤 닉네임이 중복되는 경우 4번까지 재시도하고 5번째 성공 시 닉네임을 반환한다.")
    @Test
    void generateUniqueNickname1() {
        // given
        when(accountRepository.existsByNickname(anyString()))
                .thenReturn(true)   // 1번째: 중복
                .thenReturn(true)   // 2번째: 중복
                .thenReturn(true)   // 3번째: 중복
                .thenReturn(true)   // 4번째: 중복
                .thenReturn(false); // 5번째: 성공

        // when & then
        assertThatCode(() -> nicknameCreateService.generateUniqueNickname())
                .doesNotThrowAnyException();
        verify(accountRepository, times(5)).existsByNickname(anyString());
    }

    @DisplayName("중복 예외가 5번 연속 발생하면 예외를 던진다.")
    @Test
    void generateUniqueNickname2() {
        // given
        when(accountRepository.existsByNickname(anyString()))
                .thenReturn(true)  // 모두 중복
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> nicknameCreateService.generateUniqueNickname())
                .isInstanceOf(IllegalArgumentException.class);
        verify(accountRepository, times(5)).existsByNickname(anyString());
    }
}
