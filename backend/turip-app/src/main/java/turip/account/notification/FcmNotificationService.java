package turip.account.notification;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.account.domain.FcmToken;
import turip.account.repository.AccountRepository;
import turip.infrastructure.client.FcmClient;

@Service
@RequiredArgsConstructor
public class FcmNotificationService {

    private final FcmClient fcmClient;
    private final AccountRepository accountRepository;

    public void sendToAccounts(List<Long> accountIds, FcmNotificationMessage fcmNotificationMessage) {
        List<FcmToken> fcmTokens = accountRepository.findFcmTokensByAccountIds(accountIds);

        List<String> tokens = fcmTokens.stream()
                .map(FcmToken::getToken)
                .toList();

        if (!tokens.isEmpty()) {
            List<String> invalidTokens = fcmClient.sendNotificationToMultipleDevicesAndReturnInvalidTokens(
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
