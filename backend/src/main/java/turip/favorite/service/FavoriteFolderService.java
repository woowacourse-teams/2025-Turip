package turip.favorite.service;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.common.exception.custom.ConflictException;
import turip.common.exception.custom.ForbiddenException;
import turip.common.exception.custom.NotFoundException;
import turip.favorite.controller.dto.request.FavoriteFolderNameRequest;
import turip.favorite.controller.dto.request.FavoriteFolderRequest;
import turip.favorite.controller.dto.response.FavoriteFolderResponse;
import turip.favorite.controller.dto.response.FavoriteFolderWithFavoriteStatusResponse;
import turip.favorite.controller.dto.response.FavoriteFolderWithPlaceCountResponse;
import turip.favorite.controller.dto.response.FavoriteFoldersWithFavoriteStatusResponse;
import turip.favorite.controller.dto.response.FavoriteFoldersWithPlaceCountResponse;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.repository.FavoriteFolderRepository;
import turip.favorite.repository.FavoritePlaceRepository;
import turip.member.domain.Account;
import turip.place.domain.Place;
import turip.place.repository.PlaceRepository;

@Service
@RequiredArgsConstructor
public class FavoriteFolderService {

    private final FavoriteFolderRepository favoriteFolderRepository;
    private final FavoritePlaceRepository favoritePlaceRepository;
    private final PlaceRepository placeRepository;

    @Transactional
    public void createDefaultFavoriteFolder(Account account) {
        FavoriteFolder defaultFolder = FavoriteFolder.defaultFolderOf(account);
        favoriteFolderRepository.save(defaultFolder);
    }

    @Transactional
    public FavoriteFolderResponse createCustomFavoriteFolder(FavoriteFolderRequest request, Account account) {
        FavoriteFolder favoriteFolder = FavoriteFolder.customFolderOf(account, request.name());

        validateDuplicatedName(favoriteFolder.getName(), account);
        FavoriteFolder savedFavoriteFolder = favoriteFolderRepository.save(favoriteFolder);

        return FavoriteFolderResponse.from(savedFavoriteFolder);
    }

    public FavoriteFoldersWithPlaceCountResponse findAllByMember(Account account) {
        List<FavoriteFolderWithPlaceCountResponse> favoriteFoldersWithPlaceCount = favoriteFolderRepository.findAllByAccountOrderByIdAsc(
                        account).stream()
                .map(favoriteFolder -> {
                    int placeCount = favoritePlaceRepository.countByFavoriteFolder(favoriteFolder);
                    return FavoriteFolderWithPlaceCountResponse.of(favoriteFolder, placeCount);
                })
                .toList();

        return FavoriteFoldersWithPlaceCountResponse.from(favoriteFoldersWithPlaceCount);
    }

    public FavoriteFoldersWithFavoriteStatusResponse findAllWithFavoriteStatusByAccountId(Account account,
                                                                                          Long placeId) {
        Place place = getPlaceById(placeId);
        List<FavoriteFolder> favoriteFolders = favoriteFolderRepository.findAllByAccountOrderByIdAsc(account);
        Set<Long> favoritedFolderIds = favoritePlaceRepository.findFavoriteFolderIdsByPlaceAndFavoriteFolderIn(
                place, favoriteFolders);

        List<FavoriteFolderWithFavoriteStatusResponse> favoriteFoldersWithFavoriteStatus = favoriteFolders.stream()
                .map(favoriteFolder -> {
                    boolean isFavoritePlace = favoritedFolderIds.contains(favoriteFolder.getId());
                    return FavoriteFolderWithFavoriteStatusResponse.of(favoriteFolder, isFavoritePlace);
                })
                .toList();
        return FavoriteFoldersWithFavoriteStatusResponse.from(favoriteFoldersWithFavoriteStatus);
    }

    @Transactional
    public FavoriteFolderResponse updateName(Account account, Long favoriteFolderId,
                                             FavoriteFolderNameRequest request) {
        FavoriteFolder favoriteFolder = getById(favoriteFolderId);
        if (favoriteFolder.isDefault()) {
            throw new BadRequestException(ErrorTag.DEFAULT_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED);
        }

        String newName = FavoriteFolder.formatName(request.name());
        validateOwnership(account, favoriteFolder);
        validateDuplicatedName(newName, account);
        favoriteFolder.rename(newName);

        return FavoriteFolderResponse.from(favoriteFolder);
    }

    @Transactional
    public void remove(Account account, Long favoriteFolderId) {
        FavoriteFolder favoriteFolder = getById(favoriteFolderId);

        if (favoriteFolder.isDefault()) {
            throw new BadRequestException(ErrorTag.DEFAULT_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED);
        }
        validateOwnership(account, favoriteFolder);

        favoritePlaceRepository.deleteAllByFavoriteFolder(favoriteFolder);
        favoriteFolderRepository.deleteById(favoriteFolderId);
    }

    @Transactional
    public void removeByAccount(Account account) {
        favoriteFolderRepository.findAllByAccount(account)
                .forEach(favoriteFolder -> {
                    validateOwnership(account, favoriteFolder);
                    favoritePlaceRepository.deleteAllByFavoriteFolder(favoriteFolder);
                    favoriteFolderRepository.delete(favoriteFolder);
                });
    }

    private void validateDuplicatedName(String folderName, Account account) {
        if (favoriteFolderRepository.existsByNameAndAccount(folderName, account)) {
            throw new ConflictException(ErrorTag.FAVORITE_FOLDER_NAME_CONFLICT);
        }
    }

    private Place getPlaceById(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorTag.PLACE_NOT_FOUND));
    }

    private FavoriteFolder getById(Long favoriteFolderId) {
        return favoriteFolderRepository.findById(favoriteFolderId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.FAVORITE_FOLDER_NOT_FOUND));
    }

    private void validateOwnership(Account requestAccount, FavoriteFolder favoriteFolder) {
        if (!favoriteFolder.isOwner(requestAccount)) {
            throw new ForbiddenException(ErrorTag.FORBIDDEN);
        }
    }
}
