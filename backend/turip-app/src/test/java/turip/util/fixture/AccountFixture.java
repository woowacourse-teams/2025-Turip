package turip.util.fixture;

import java.util.UUID;
import turip.account.domain.Account;
import turip.account.domain.Role;

public class AccountFixture {

    public static Account createUser() {
        return new Account(1L, Role.USER, UUID.randomUUID().toString().substring(0, 8));
    }

    public static Account createEntity() {
        return new Account(Role.USER, UUID.randomUUID().toString().substring(0, 8));
    }

    public static Account createAdmin() {
        return new Account(1L, Role.ADMIN, UUID.randomUUID().toString().substring(0, 8));
    }

    public static Account createCustomAccount(Long id, Role role) {
        return new Account(id, role, UUID.randomUUID().toString().substring(0, 8));
    }
}
