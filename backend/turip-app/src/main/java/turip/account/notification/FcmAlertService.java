package turip.account.notification;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.account.domain.Account;
import turip.account.domain.FcmToken;
import turip.account.repository.AccountRepository;
import turip.infrastructure.client.FirebaseClient;

@Service
@RequiredArgsConstructor
public class FcmAlertService {

    private final FirebaseClient firebaseClient;
    private final AccountRepository accountRepository;

    public void sendToAccounts(List<Account> accounts, FcmAlertMessage fcmAlertMessage) {
        List<Long> accountIds = accounts.stream()
                .map(Account::getId)
                .toList();

        List<FcmToken> fcmTokens = accountRepository.findFcmTokensByAccountIds(accountIds);

        List<String> tokens = fcmTokens.stream()
                .map(FcmToken::getToken)
                .toList();

        if (!tokens.isEmpty()) {
            firebaseClient.sendNotificationToMultipleDevices(
                    tokens,
                    fcmAlertMessage.title(),
                    fcmAlertMessage.message()
            );
        }
    }
}
