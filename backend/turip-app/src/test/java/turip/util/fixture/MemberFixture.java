package turip.util.fixture;

import turip.account.domain.Account;
import turip.account.domain.Member;

public class MemberFixture {

    public static Member createMember() {
        Account account = AccountFixture.createUser();
        return new Member(account, "user@gmail.com", true);
    }

    public static Member createCustomMember(Account account, String email, boolean isFirstLogin) {
        return new Member(account, email, isFirstLogin);
    }
}
