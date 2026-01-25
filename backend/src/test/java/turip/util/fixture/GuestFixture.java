package turip.util.fixture;

import turip.account.domain.Account;
import turip.account.domain.Guest;

public class GuestFixture {

    public static Guest createCustomGuest(Account account, String deviceFid) {
        return new Guest(account, deviceFid);
    }
}
