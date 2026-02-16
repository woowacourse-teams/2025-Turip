package turip.favorite.service;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.Account;
import turip.account.domain.Member;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.common.exception.custom.ConflictException;
import turip.common.exception.custom.NotFoundException;
import turip.favorite.controller.dto.request.FavoriteFolderNameRequest;
import turip.favorite.controller.dto.request.FavoriteFolderRequest;
import turip.favorite.controller.dto.response.FavoriteFolderDetailResponse;
import turip.favorite.controller.dto.response.FavoriteFolderExitResponse;
import turip.favorite.controller.dto.response.FavoriteFolderJoinResponse;
import turip.favorite.controller.dto.response.FavoriteFolderMembersResponse;
import turip.favorite.controller.dto.response.FavoriteFolderExitResponse;
import turip.favorite.controller.dto.response.FavoriteFolderJoinResponse;
import turip.favorite.controller.dto.response.FavoriteFolderResponse;
import turip.favorite.controller.dto.response.FavoriteFolderWithFavoriteStatusResponse;
import turip.favorite.controller.dto.response.FavoriteFoldersDetailResponse;
import turip.favorite.controller.dto.response.FavoriteFoldersWithFavoriteStatusResponse;
import turip.favorite.controller.dto.response.FavoriteFoldersWithPlaceCountResponse;
import turip.favorite.domain.AccountRole;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.domain.event.ActionType;
import turip.favorite.domain.event.FavoriteFolderUpdateEvent;
import turip.favorite.domain.FavoriteFolderAccount;
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
    private final ApplicationEventPublisher eventPublisher;

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

    @Transactional
    public FavoriteFolderJoinResponse joinMember(Long favoriteFolderId, Member member) {
        FavoriteFolder favoriteFolder = getById(favoriteFolderId);
        validateShareAndCustomFolder(favoriteFolder);

        FavoriteFolderAccount favoriteFolderAccount = favoriteFolderAccountService.findOrCreate(favoriteFolder,
                member.getAccount());
        return FavoriteFolderJoinResponse.from(favoriteFolderAccount);
    }

    public FavoriteFolder getById(Long favoriteFolderId) {
        return favoriteFolderRepository.findById(favoriteFolderId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.FAVORITE_FOLDER_NOT_FOUND));
    }

    public FavoriteFoldersWithPlaceCountResponse findAllByAccount(Account account) {
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

    public FavoriteFolderDetailResponse findById(Long favoriteFolderId, Account account) {
        FavoriteFolder favoriteFolder = getById(favoriteFolderId);
        favoriteFolderAccountService.validateMembership(account, favoriteFolder);

        int placeCount = favoritePlaceRepository.countByFavoriteFolder(favoriteFolder);
        int memberCount = favoriteFolderAccountService.countByFavoriteFolder(favoriteFolder);
        return FavoriteFolderDetailResponse.of(favoriteFolder, account, placeCount, memberCount);
    }

    public FavoriteFolderMembersResponse findMembersById(Long favoriteFolderId, Account account) {
        FavoriteFolder favoriteFolder = getById(favoriteFolderId);
        favoriteFolderAccountService.validateMembership(account, favoriteFolder);

        List<Member> members = favoriteFolderAccountService.findMembersByFavoriteFolder(favoriteFolder);
        return FavoriteFolderMembersResponse.of(members);
    }

    @Transactional
    public FavoriteFolderResponse updateName(Account account, Long favoriteFolderId,
                                             FavoriteFolderNameRequest request) {
        FavoriteFolder favoriteFolder = getById(favoriteFolderId);
        if (favoriteFolder.isDefault()) {
            throw new BadRequestException(ErrorTag.DEFAULT_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED);
        }

        String newName = FavoriteFolder.formatName(request.name());
        favoriteFolderAccountService.validateMembership(account, favoriteFolder);
        validateDuplicatedName(newName, account);
        favoriteFolder.rename(newName);

        eventPublisher.publishEvent(FavoriteFolderUpdateEvent.of(favoriteFolderId, ActionType.FOLDER_NAME_CHANGED));

        return FavoriteFolderResponse.of(favoriteFolder, account);
    }

    @Transactional
    public void remove(Account account, Long favoriteFolderId) {
        FavoriteFolder favoriteFolder = getById(favoriteFolderId);
        validateRemovableFolder(account, favoriteFolder);
        favoritePlaceRepository.deleteAllByFavoriteFolder(favoriteFolder);
        favoriteFolderRepository.deleteById(favoriteFolderId);

        eventPublisher.publishEvent(FavoriteFolderUpdateEvent.of(favoriteFolderId, ActionType.FOLDER_DELETED));
    }

    @Transactional
    public FavoriteFolderExitResponse exitFolder(Account account, Long favoriteFolderId) {
        FavoriteFolder favoriteFolder = getByIdWithLock(favoriteFolderId);
        validateShareAndCustomFolder(favoriteFolder);
        favoriteFolderAccountService.validateMembership(account, favoriteFolder);
        favoriteFolderAccountService.deleteByFavoriteFolderAndAccount(favoriteFolder, account);

        boolean isDeleted = false;
        int remainingMemberCount = favoriteFolderAccountService.countByFavoriteFolder(favoriteFolder);
        if (remainingMemberCount == 0) {
            removeFavoriteFolderWithFavoritePlaces(favoriteFolderId, favoriteFolder);
            isDeleted = true;
        }

        return FavoriteFolderExitResponse.of(isDeleted);
    }

    private void validateRemovableFolder(Account account, FavoriteFolder favoriteFolder) {
        if (favoriteFolder.isDefault()) {
            throw new BadRequestException(ErrorTag.DEFAULT_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED);
        }
        if (favoriteFolder.isShared()) {
            throw new BadRequestException(ErrorTag.SHARED_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED);
        }
        favoriteFolderAccountService.validateOwnership(account, favoriteFolder);
    }

    private void validateDuplicatedName(String folderName, Account account) {
        favoriteFolderRepository.findAllByAccount(account)
                .forEach(favoriteFolder -> {
                    if (!favoriteFolder.isShared() && favoriteFolder.isSameFolderName(folderName)) {
                        throw new ConflictException(ErrorTag.FAVORITE_FOLDER_NAME_CONFLICT);
                    }
                });
    }

    private void validateShareAndCustomFolder(FavoriteFolder favoriteFolder) {
        if (!favoriteFolder.isShared()) {
            throw new BadRequestException(ErrorTag.PERSONAL_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED);
        }
        if (favoriteFolder.isDefault()) {
            throw new BadRequestException(ErrorTag.DEFAULT_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED);
        }
    }

    private Place getPlaceById(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorTag.PLACE_NOT_FOUND));
    }

    private FavoriteFolder getByIdWithLock(Long favoriteFolderId) {
        return favoriteFolderRepository.findByIdWithLock(favoriteFolderId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.FAVORITE_FOLDER_NOT_FOUND));
    }

    private void removeFavoriteFolderWithFavoritePlaces(Long favoriteFolderId, FavoriteFolder favoriteFolder) {
        favoritePlaceRepository.deleteAllByFavoriteFolder(favoriteFolder);
        favoriteFolderRepository.deleteById(favoriteFolderId);
    }
}
