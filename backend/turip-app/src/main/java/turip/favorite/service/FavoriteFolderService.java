package turip.favorite.service;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.Account;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.common.exception.custom.ConflictException;
import turip.common.exception.custom.NotFoundException;
import turip.favorite.controller.dto.request.FavoriteFolderNameRequest;
import turip.favorite.controller.dto.request.FavoriteFolderRequest;
import turip.favorite.controller.dto.response.FavoriteFolderResponse;
import turip.favorite.controller.dto.response.FavoriteFolderWithFavoriteStatusResponse;
import turip.favorite.controller.dto.response.FavoriteFolderWithPlaceCountResponse;
import turip.favorite.controller.dto.response.FavoriteFoldersWithFavoriteStatusResponse;
import turip.favorite.controller.dto.response.FavoriteFoldersWithPlaceCountResponse;
import turip.favorite.domain.AccountRole;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.repository.FavoriteFolderRepository;
import turip.favorite.repository.FavoritePlaceRepository;
import turip.place.domain.Place;
import turip.place.repository.PlaceRepository;

@Service
@RequiredArgsConstructor
public class FavoriteFolderService {

    private final FavoriteFolderRepository favoriteFolderRepository;
    private final FavoritePlaceRepository favoritePlaceRepository;
    private final PlaceRepository placeRepository;
    private final FavoriteFolderAccountService favoriteFolderAccountService;

    @Transactional
    public void createDefaultFavoriteFolder(Account account) {
        FavoriteFolder defaultFolder = FavoriteFolder.defaultFolderOf();
        FavoriteFolder savedFavoriteFolder = favoriteFolderRepository.save(defaultFolder);
        favoriteFolderAccountService.save(savedFavoriteFolder, account, AccountRole.OWNER);
    }

    @Transactional
    public FavoriteFolderResponse createCustomFavoriteFolder(FavoriteFolderRequest request, Account account) {
        FavoriteFolder favoriteFolder = FavoriteFolder.customFolderOf(request.name());

        validateDuplicatedName(favoriteFolder.getName(), account);
        FavoriteFolder savedFavoriteFolder = favoriteFolderRepository.save(favoriteFolder);
        favoriteFolderAccountService.save(savedFavoriteFolder, account, AccountRole.OWNER);

        return FavoriteFolderResponse.of(savedFavoriteFolder, account);
    }

    public FavoriteFoldersWithPlaceCountResponse findAllByMember(Account account) {
        List<FavoriteFolderWithPlaceCountResponse> favoriteFoldersWithPlaceCount = favoriteFolderRepository.findAllByAccountOrderByFavoriteFolderAccountIdAsc(
                        account).stream()
                .map(favoriteFolder -> {
                    int placeCount = favoritePlaceRepository.countByFavoriteFolder(favoriteFolder);
                    return FavoriteFolderWithPlaceCountResponse.of(favoriteFolder, account, placeCount);
                })
                .toList();

        return FavoriteFoldersWithPlaceCountResponse.from(favoriteFoldersWithPlaceCount);
    }

    public FavoriteFoldersWithFavoriteStatusResponse findAllWithFavoriteStatusByAccountId(Account account,
                                                                                          Long placeId) {
        Place place = getPlaceById(placeId);
        List<FavoriteFolder> favoriteFolders = favoriteFolderRepository.findAllByAccountOrderByFavoriteFolderAccountIdAsc(
                account);
        Set<Long> favoritedFolderIds = favoritePlaceRepository.findFavoriteFolderIdsByPlaceAndFavoriteFolderIn(
                place, favoriteFolders);

        List<FavoriteFolderWithFavoriteStatusResponse> favoriteFoldersWithFavoriteStatus = favoriteFolders.stream()
                .map(favoriteFolder -> {
                    boolean isFavoritePlace = favoritedFolderIds.contains(favoriteFolder.getId());
                    return FavoriteFolderWithFavoriteStatusResponse.of(favoriteFolder, account, isFavoritePlace);
                })
                .toList();
        return FavoriteFoldersWithFavoriteStatusResponse.from(favoriteFoldersWithFavoriteStatus);
    }

    public boolean isCustomFolderExists(Account account) {
        return favoriteFolderRepository.existsCustomFolderByAccount(account);
    }

    @Transactional
    public FavoriteFolderResponse updateName(Account account, Long favoriteFolderId,
                                             FavoriteFolderNameRequest request) {
        FavoriteFolder favoriteFolder = getById(favoriteFolderId);
        if (favoriteFolder.isDefault()) {
            throw new BadRequestException(ErrorTag.DEFAULT_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED);
        }

        String newName = FavoriteFolder.formatName(request.name());
        favoriteFolderAccountService.validateOwnership(account, favoriteFolder);
        validateDuplicatedName(newName, account);
        favoriteFolder.rename(newName);

        return FavoriteFolderResponse.of(favoriteFolder, account);
    }

    @Transactional
    public void remove(Account account, Long favoriteFolderId) {
        FavoriteFolder favoriteFolder = getById(favoriteFolderId);

        if (favoriteFolder.isDefault()) {
            throw new BadRequestException(ErrorTag.DEFAULT_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED);
        }
        favoriteFolderAccountService.validateOwnership(account, favoriteFolder);

        favoritePlaceRepository.deleteAllByFavoriteFolder(favoriteFolder);
        favoriteFolderRepository.deleteById(favoriteFolderId);
    }

    private void validateDuplicatedName(String folderName, Account account) {
        favoriteFolderRepository.findAllByAccount(account)
                .forEach(favoriteFolder -> {
                    if (!favoriteFolder.isShared() && favoriteFolder.isFolderName(folderName)) {
                        throw new ConflictException(ErrorTag.FAVORITE_FOLDER_NAME_CONFLICT);
                    }
                });
    }

    private Place getPlaceById(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorTag.PLACE_NOT_FOUND));
    }

    private FavoriteFolder getById(Long favoriteFolderId) {
        return favoriteFolderRepository.findById(favoriteFolderId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.FAVORITE_FOLDER_NOT_FOUND));
    }
}
