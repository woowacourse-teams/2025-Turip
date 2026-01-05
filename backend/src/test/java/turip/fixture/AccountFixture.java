package turip.fixture;

import turip.account.domain.Account;
import turip.account.domain.Role;

public class AccountFixture {

    public static Account createUser() {
        return new Account(1L, Role.USER);
    }

    public static Account createAdmin() {
        return new Account(1L, Role.ADMIN);
    }

    public static Account createCustomAccount(Long id, Role role) {
        return new Account(id, role);
    }
}
