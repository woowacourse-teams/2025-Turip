package turip.event;

import java.util.List;
import org.springframework.stereotype.Service;
import turip.account.domain.Account;

@Service
public class FcmAlertService {

    // TODO: FirebaseClient 연결
    public void sendToAccounts(List<Account> accounts, FcmAlertMessage fcmAlertMessage) {

    }
}
