package turip.account.notification;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.account.domain.Account;
import turip.account.domain.FcmToken;
import turip.account.domain.Role;
import turip.account.repository.AccountRepository;
import turip.infrastructure.client.FirebaseClient;

@ExtendWith(MockitoExtension.class)
class FcmAlertServiceTest {

    @Mock
    private FirebaseClient firebaseClient;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private FcmAlertService fcmAlertService;

    @Nested
    class SendToAccounts {

        @Test
        @DisplayName("계정 리스트에 FCM 알림을 성공적으로 전송한다")
        void sendToAccounts1() {
            // given
            Account account1 = createAccount(1L);
            Account account2 = createAccount(2L);
            List<Account> accounts = List.of(account1, account2);

            FcmToken fcmToken1 = new FcmToken(account1, "token1");
            FcmToken fcmToken2 = new FcmToken(account2, "token2");
            List<FcmToken> fcmTokens = List.of(fcmToken1, fcmToken2);

            FcmAlertMessage message = FcmAlertMessage.of("테스트 제목", "테스트 내용");

            when(accountRepository.findFcmTokensByAccountIds(List.of(1L, 2L)))
                    .thenReturn(fcmTokens);

            // when
            fcmAlertService.sendToAccounts(accounts, message);

            // then
            verify(accountRepository, times(1)).findFcmTokensByAccountIds(List.of(1L, 2L));
            verify(firebaseClient, times(1)).sendNotificationToMultipleDevices(
                    eq(List.of("token1", "token2")),
                    eq("테스트 제목"),
                    eq("테스트 내용")
            );
        }

        @Test
        @DisplayName("FCM 토큰이 없을 때 알림을 전송하지 않는다")
        void sendToAccounts2() {
            // given
            Account account = createAccount(1L);
            List<Account> accounts = List.of(account);

            FcmAlertMessage message = FcmAlertMessage.of("테스트 제목", "테스트 내용");

            when(accountRepository.findFcmTokensByAccountIds(List.of(1L)))
                    .thenReturn(List.of());

            // when
            fcmAlertService.sendToAccounts(accounts, message);

            // then
            verify(accountRepository, times(1)).findFcmTokensByAccountIds(List.of(1L));
            verify(firebaseClient, never()).sendNotificationToMultipleDevices(anyList(), anyString(), anyString());
        }

        @Test
        @DisplayName("여러 계정 중 알림이 활성화된 토큰만 조회하여 알림을 전송한다")
        void sendToAccounts3() {
            // given
            Account account1 = createAccount(1L);
            Account account2 = createAccount(2L);
            Account account3 = createAccount(3L);
            List<Account> accounts = List.of(account1, account2, account3);

            // account1, account2만 알림 활성화된 토큰
            FcmToken fcmToken1 = new FcmToken(account1, "token1");
            FcmToken fcmToken2 = new FcmToken(account2, "token2");
            List<FcmToken> fcmTokens = List.of(fcmToken1, fcmToken2);

            FcmAlertMessage message = FcmAlertMessage.of("테스트 제목", "테스트 내용");

            when(accountRepository.findFcmTokensByAccountIds(List.of(1L, 2L, 3L)))
                    .thenReturn(fcmTokens);

            // when
            fcmAlertService.sendToAccounts(accounts, message);

            // then
            verify(accountRepository, times(1)).findFcmTokensByAccountIds(List.of(1L, 2L, 3L));
            verify(firebaseClient, times(1)).sendNotificationToMultipleDevices(
                    eq(List.of("token1", "token2")),
                    eq("테스트 제목"),
                    eq("테스트 내용")
            );
        }
    }

    private Account createAccount(Long id) {
        return new Account(id, Role.USER, "nickname" + id);
    }
}
