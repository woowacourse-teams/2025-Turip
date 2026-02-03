package turip.util.fixture;

import java.util.UUID;
import turip.account.domain.Account;
import turip.account.domain.Member;

public class MemberFixture {

    public static Member createMember() {
        Account account = AccountFixture.createUser();
        return new Member(account, "user@gmail.com", UUID.randomUUID().toString().substring(0, 8), true);
    }

    public static Member createCustomMember(Account account, String email, boolean isFirstLogin) {
        return new Member(account, email, UUID.randomUUID().toString().substring(0, 8), isFirstLogin);
    }
}
