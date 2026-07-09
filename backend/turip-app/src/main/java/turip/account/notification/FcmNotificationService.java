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
public class FcmNotificationService {

    private final FirebaseClient firebaseClient;
    private final AccountRepository accountRepository;

    public void sendToAccounts(List<Account> accounts, FcmNotificationMessage fcmNotificationMessage) {
        List<Long> accountIds = accounts.stream()
                .map(Account::getId)
                .toList();

        List<FcmToken> fcmTokens = accountRepository.findFcmTokensByAccountIds(accountIds);

        List<String> tokens = fcmTokens.stream()
                .map(FcmToken::getToken)
                .toList();

        if (!tokens.isEmpty()) {
            List<String> invalidTokens = firebaseClient.sendNotificationToMultipleDevicesAndReturnInvalidTokens(
                    tokens,
                    fcmNotificationMessage.title(),
                    fcmNotificationMessage.message()
            );

            if (!invalidTokens.isEmpty()) {
                accountRepository.deleteFcmTokensByTokenIn(invalidTokens);
            }
        }
    }
}
