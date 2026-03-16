package turip.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.controller.dto.response.MigrationAvailabilityResponse;
import turip.account.domain.Account;
import turip.account.domain.Guest;
import turip.account.repository.GuestRepository;
import turip.favorite.service.FavoriteContentService;
import turip.favorite.service.FavoriteFolderService;
import turip.favorite.service.FavoritePlaceService;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final AccountService accountService;
    private final GuestRepository guestRepository;
    private final FavoriteContentService favoriteContentService;
    private final FavoritePlaceService favoritePlaceService;
    private final FavoriteFolderService favoriteFolderService;
    private final GuestNicknameCreator guestNicknameCreator;

    @Transactional
    public Guest findOrCreateByDeviceFid(String deviceFid) {
        return guestRepository.findByDeviceFid(deviceFid)
                .orElseGet(() -> {
                    Account savedAccount = accountService.create(guestNicknameCreator);
                    return guestRepository.save(new Guest(savedAccount, deviceFid));
                });
    }

    public MigrationAvailabilityResponse checkMigrationAvailability(Guest guest) {
        boolean isFavoriteContentExists = favoriteContentService.existsByAccount(guest.getAccount());
        boolean isFavoritePlaceExists = favoritePlaceService.existsByAccount(guest.getAccount());
        boolean isCustomFolderExists = favoriteFolderService.isCustomFolderExists(guest.getAccount());

        if (isFavoriteContentExists || isFavoritePlaceExists || isCustomFolderExists) {
            return MigrationAvailabilityResponse.from(true);
        }
        return MigrationAvailabilityResponse.from(false);
    }

    @Transactional
    public void delete(Guest guest) {
        guestRepository.delete(guest);
        accountService.deleteAccountAndFavorites(guest.getAccount());
    }
}
