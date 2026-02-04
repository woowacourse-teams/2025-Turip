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
import turip.account.domain.Role;
import turip.account.repository.AccountRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AccountCreateServiceTest.RetryTestConfig.class)
public class AccountCreateServiceTest {

    @Autowired
    private AccountCreateService accountCreateService;

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
        AccountCreateService accountCreateService(AccountRepository accountRepository) {
            return new AccountCreateService(accountRepository);
        }
    }

    @BeforeEach
    void setUp() {
        reset(accountRepository);
    }

    @DisplayName("랜덤 닉네임이 중복되는 경우 4번까지 재시도하고 5번째 성공 시 저장된다.")
    @Test
    void createUserAccount1() {
        // given
        Account savedAccount = new Account(1L, Role.USER, "테스트 닉네임");
        when(accountRepository.save(any(Account.class)))
                .thenThrow(new DuplicateKeyException("dup1"))
                .thenThrow(new DuplicateKeyException("dup2"))
                .thenThrow(new DuplicateKeyException("dup3"))
                .thenThrow(new DuplicateKeyException("dup4"))
                .thenReturn(savedAccount);

        // when & then
        assertThatCode(() -> accountCreateService.createUserAccount())
                .doesNotThrowAnyException();
        verify(accountRepository, times(5)).save(any(Account.class));
    }

    @DisplayName("중복 예외가 5번 연속 발생하면 예외를 던진다.")
    @Test
    void createUserAccount2() {
        // given
        when(accountRepository.save(any(Account.class)))
                .thenThrow(new DuplicateKeyException("dup1"))
                .thenThrow(new DuplicateKeyException("dup2"))
                .thenThrow(new DuplicateKeyException("dup3"))
                .thenThrow(new DuplicateKeyException("dup4"))
                .thenThrow(new DuplicateKeyException("dup5"));

        // when & then
        assertThatThrownBy(() -> accountCreateService.createUserAccount())
                .isInstanceOf(DuplicateKeyException.class);
        verify(accountRepository, times(5)).save(any(Account.class));
    }
}
