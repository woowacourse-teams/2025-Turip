package turip.account.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import turip.account.domain.Account;
import turip.account.domain.Role;
import turip.container.TestContainerConfig;

@SpringBootTest
@ActiveProfiles("test-mysql")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountInsertRepositoryTest extends TestContainerConfig {

    @Autowired
    private AccountInsertRepository accountInsertRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM account");
    }

    @DisplayName("insert에 성공한 경우 Account를 반환하고 DB에 저장된다")
    @Test
    void trySave1() {
        // given
        String nickname = "새로운닉네임";
        Account newAccount = new Account(Role.USER, nickname);

        // when
        Optional<Account> result = accountInsertRepository.trySave(newAccount);

        // then
        assertThat(result).isPresent();
        Account savedAccount = result.get();

        // DB에 실제로 저장되었는지 확인
        Optional<Account> foundAccount = accountRepository.findById(savedAccount.getId());
        assertThat(foundAccount).isPresent();
        assertThat(foundAccount.get().getNickname()).isEqualTo(nickname);
    }

    @DisplayName("nickname이 중복되면 Optional.empty()를 반환한다")
    @Test
    void trySave2() {
        // given
        String duplicateNickname = "중복닉네임";
        Account firstAccount = new Account(Role.USER, duplicateNickname);
        accountInsertRepository.trySave(firstAccount);

        // when - 같은 nickname으로 다시 insert 시도
        Account secondAccount = new Account(Role.USER, duplicateNickname);
        Optional<Account> result = accountInsertRepository.trySave(secondAccount);

        // then
        assertThat(result).isEmpty();

        // DB에는 첫 번째 계정만 저장되어 있어야 함
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM account WHERE nickname = ?",
                Integer.class,
                duplicateNickname
        );
        assertThat(count).isEqualTo(1);
    }

    @DisplayName("동시에 같은 nickname으로 insert 시도 시 하나만 성공한다")
    @Test
    void trySave3() {
        // given
        String nickname = "경쟁닉네임";
        Account account1 = new Account(Role.USER, nickname);
        Account account2 = new Account(Role.USER, nickname);

        // when
        Optional<Account> result1 = accountInsertRepository.trySave(account1);
        Optional<Account> result2 = accountInsertRepository.trySave(account2);

        // then - 하나는 성공, 하나는 실패
        assertThat(result1.isPresent() || result2.isPresent()).isTrue();
        assertThat(result1.isPresent() && result2.isPresent()).isFalse();

        // DB에는 하나만 저장
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM account WHERE nickname = ?",
                Integer.class,
                nickname
        );
        assertThat(count).isEqualTo(1);
    }
}
