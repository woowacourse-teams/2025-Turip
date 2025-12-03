package turip.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.Account;
import turip.account.domain.Guest;
import turip.account.repository.GuestRepository;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final AccountService accountService;
    private final GuestRepository guestRepository;

    @Transactional
    public Guest findOrCreateByDeviceFid(String deviceFid) {
        return guestRepository.findByDeviceFid(deviceFid)
                .orElseGet(() -> {
                    Account savedAccount = accountService.create();
                    return guestRepository.save(new Guest(savedAccount, deviceFid));
                });
    }

    @Transactional
    public void delete(Guest guest) {
        guestRepository.delete(guest);
        accountService.deleteAccountAndFavorites(guest.getAccount());
    }
}
