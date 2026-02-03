package turip.favorite.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.Account;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.common.exception.custom.ConflictException;
import turip.common.exception.custom.NotFoundException;
import turip.favorite.controller.dto.request.FavoritePlaceOrderRequest;
import turip.favorite.controller.dto.response.FavoriteFolderWithFavoriteStatusResponse.FavoritePlaceResponse;
import turip.favorite.controller.dto.response.FavoriteFolderWithFavoriteStatusResponse.FavoritePlaceWithPlaceDetailResponse;
import turip.favorite.controller.dto.response.FavoriteFolderWithFavoriteStatusResponse.FavoritePlacesWithPlaceDetailResponse;
import turip.favorite.controller.dto.response.FavoritePlaceCountResponse;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.domain.FavoritePlace;
import turip.favorite.repository.FavoriteFolderRepository;
import turip.favorite.repository.FavoritePlaceRepository;
import turip.place.domain.Place;
import turip.place.repository.PlaceRepository;

@Service
@RequiredArgsConstructor
public class FavoritePlaceService {

    private final FavoritePlaceRepository favoritePlaceRepository;
    private final FavoriteFolderRepository favoriteFolderRepository;
    private final PlaceRepository placeRepository;
    private final FavoriteFolderAccountService favoriteFolderAccountService;

    @Transactional
    public FavoritePlaceResponse create(Account account, Long favoriteFolderId, Long placeId) {
        FavoriteFolder favoriteFolder = getFavoriteFolderById(favoriteFolderId);
        Place place = getPlaceById(placeId);

        favoriteFolderAccountService.validateOwnership(account, favoriteFolder);
        validateDuplicated(favoriteFolder, place);

        Integer maxOrder = favoritePlaceRepository.findMaxFavoriteOrderByFavoriteFolder(favoriteFolder)
                .orElse(0);

        FavoritePlace favoritePlace = new FavoritePlace(favoriteFolder, place, maxOrder + 1);
        FavoritePlace savedFavoritePlace = favoritePlaceRepository.save(favoritePlace);

        return FavoritePlaceResponse.from(savedFavoritePlace);
    }

    @Transactional
    public List<FavoritePlaceResponse> updateFavoriteFolders(Account account,
                                                             List<Long> favoriteFolderIds,
                                                             Long placeId
    ) {
        List<Long> requestFavoriteFolderIds = favoriteFolderIds.stream().distinct().toList();
        List<FavoriteFolder> favoriteFolders = favoriteFolderRepository.findAllById(requestFavoriteFolderIds);
        validateMultiFolder(account, favoriteFolders, requestFavoriteFolderIds);

        Place place = getPlaceById(placeId);
        List<FavoritePlace> existingFavoritePlaces = favoritePlaceRepository.findAllByPlaceAndAccount(place, account);

        deleteRemovedFavoritePlaces(existingFavoritePlaces, requestFavoriteFolderIds);
        List<FavoritePlace> createdFavoritePlaces = createFavoritePlaces(place, existingFavoritePlaces, favoriteFolders,
                requestFavoriteFolderIds);

        return convertToResultResponse(existingFavoritePlaces, createdFavoritePlaces, requestFavoriteFolderIds);
    }

    public FavoritePlacesWithPlaceDetailResponse findAllByFolder(Long favoriteFolderId) {
        FavoriteFolder favoriteFolder = getFavoriteFolderById(favoriteFolderId);

        List<FavoritePlaceWithPlaceDetailResponse> favoritePlaces = favoritePlaceRepository.findAllByFavoriteFolderOrderByFavoriteOrderAsc(
                        favoriteFolder).stream()
                .map(FavoritePlaceWithPlaceDetailResponse::from)
                .toList();

        return FavoritePlacesWithPlaceDetailResponse.from(favoritePlaces);
    }

    public FavoritePlaceCountResponse countByAccount(Account account) {
        int count = favoritePlaceRepository.countByAccount(account);
        return FavoritePlaceCountResponse.from(count);
    }

    public boolean existsByAccount(Account account) {
        return favoritePlaceRepository.existsByAccount(account);
    }

    @Transactional
    public void updatePlaceOrder(Account account, Long favoriteFolderId,
                                 FavoritePlaceOrderRequest request) {
        FavoriteFolder favoriteFolder = getFavoriteFolderById(favoriteFolderId);
        favoriteFolderAccountService.validateOwnership(account, favoriteFolder);

        List<Long> favoritePlaceIdsOrder = request.favoritePlaceIdsOrder();

        for (int index = 0; index < favoritePlaceIdsOrder.size(); index++) {
            Long favoritePlaceId = favoritePlaceIdsOrder.get(index);
            FavoritePlace favoritePlace = getFavoritePlaceById(favoritePlaceId);
            validateFavoritePlaceBelongsToFolder(favoritePlace, favoriteFolder);
            favoritePlace.updateFavoriteOrder(index + 1);
        }
    }

    @Transactional
    public void remove(Account account, Long favoriteFolderId, Long placeId) {
        FavoriteFolder favoriteFolder = getFavoriteFolderById(favoriteFolderId);
        Place place = getPlaceById(placeId);

        favoriteFolderAccountService.validateOwnership(account, favoriteFolder);
        FavoritePlace favoritePlace = getByFavoriteFolderAndPlace(favoriteFolder, place);

        favoritePlaceRepository.delete(favoritePlace);
    }

    private FavoriteFolder getFavoriteFolderById(Long favoriteFolderId) {
        return favoriteFolderRepository.findById(favoriteFolderId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.FAVORITE_FOLDER_NOT_FOUND));
    }

    private Place getPlaceById(Long placeId) {
        return placeRepository.findById(placeId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.PLACE_NOT_FOUND));
    }

    private FavoritePlace getFavoritePlaceById(Long favoritePlaceId) {
        return favoritePlaceRepository.findById(favoritePlaceId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.FAVORITE_PLACE_NOT_FOUND));
    }

    private void deleteRemovedFavoritePlaces(List<FavoritePlace> existingFavoritePlaces,
                                             List<Long> requestFavoriteFolderIds) {
        List<FavoritePlace> removedFavoritePlaces = existingFavoritePlaces.stream()
                .filter(fp -> !requestFavoriteFolderIds.contains(fp.getFavoriteFolder().getId()))
                .toList();
        favoritePlaceRepository.deleteAll(removedFavoritePlaces);
    }

    private List<FavoritePlace> createFavoritePlaces(Place place,
                                                     List<FavoritePlace> existingFavoritePlaces,
                                                     List<FavoriteFolder> favoriteFolders,
                                                     List<Long> requestFavoriteFolderIds
    ) {
        List<Long> existingFavoriteFolderIds = existingFavoritePlaces.stream()
                .map(fp -> fp.getFavoriteFolder().getId())
                .toList();

        Map<Long, FavoriteFolder> favoriteFolderRegistry = favoriteFolders.stream()
                .collect(Collectors.toMap(FavoriteFolder::getId, f -> f));

        List<FavoritePlace> newFavoritePlaces = requestFavoriteFolderIds.stream()
                .filter(id -> !existingFavoriteFolderIds.contains(id))
                .map(id -> {
                    FavoriteFolder folder = favoriteFolderRegistry.get(id);
                    int nextOrder = getNextOrder(folder);
                    return new FavoritePlace(folder, place, nextOrder);
                })
                .toList();

        return favoritePlaceRepository.saveAll(newFavoritePlaces);
    }

    private int getNextOrder(FavoriteFolder folder) {
        return favoritePlaceRepository.findMaxFavoriteOrderByFavoriteFolder(folder).orElse(0) + 1;
    }

    private List<FavoritePlaceResponse> convertToResultResponse(List<FavoritePlace> existingFavoritePlaces,
                                                                List<FavoritePlace> createdFavoritePlaces,
                                                                List<Long> requestFavoriteFolderIds
    ) {
        List<FavoritePlace> remainedFavoritePlaces = existingFavoritePlaces.stream()
                .filter(fp -> requestFavoriteFolderIds.contains(fp.getFavoriteFolder().getId()))
                .collect(Collectors.toList());

        remainedFavoritePlaces.addAll(createdFavoritePlaces);
        return remainedFavoritePlaces.stream()
                .map(FavoritePlaceResponse::from)
                .toList();
    }

    private void validateMultiFolder(Account account, List<FavoriteFolder> requestedFavoriteFolders,
                                     List<Long> requestedFavoriteFolderIds) {
        if (requestedFavoriteFolders.size() != requestedFavoriteFolderIds.size()) {
            throw new NotFoundException(ErrorTag.FAVORITE_FOLDER_NOT_FOUND);
        }
        requestedFavoriteFolders.forEach(folder -> favoriteFolderAccountService.validateOwnership(account, folder));
    }

    private void validateDuplicated(FavoriteFolder favoriteFolder, Place place) {
        boolean isAlreadyFavorite = favoritePlaceRepository.existsByFavoriteFolderAndPlace(favoriteFolder, place);
        if (isAlreadyFavorite) {
            throw new ConflictException(ErrorTag.FAVORITE_PLACE_IN_FOLDER_CONFLICT);
        }
    }

    private void validateFavoritePlaceBelongsToFolder(FavoritePlace favoritePlace, FavoriteFolder favoriteFolder) {
        if (!favoritePlace.getFavoriteFolder().equals(favoriteFolder)) {
            throw new BadRequestException(ErrorTag.FAVORITE_PLACE_FOLDER_MISMATCH);
        }
    }

    private FavoritePlace getByFavoriteFolderAndPlace(FavoriteFolder favoriteFolder, Place place) {
        return favoritePlaceRepository.findByFavoriteFolderAndPlace(favoriteFolder, place)
                .orElseThrow(() -> new NotFoundException(ErrorTag.FAVORITE_PLACE_NOT_FOUND));
    }
}
