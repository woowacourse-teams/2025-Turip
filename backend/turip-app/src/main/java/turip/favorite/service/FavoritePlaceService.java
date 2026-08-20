package turip.favorite.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
import turip.favorite.domain.event.ActionType;
import turip.favorite.domain.event.FavoriteFolderUpdateEvent;
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
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public FavoritePlaceResponse create(Account account, Long favoriteFolderId, Long placeId) {
        FavoriteFolder favoriteFolder = getFavoriteFolderByIdWithLock(favoriteFolderId);
        Place place = getPlaceById(placeId);

        favoriteFolderAccountService.validateMembership(account, favoriteFolder);
        validateDuplicated(favoriteFolder, place);

        Integer maxOrder = favoritePlaceRepository.findMaxFavoriteOrderByFavoriteFolder(favoriteFolder)
                .orElse(0);

        FavoritePlace favoritePlace = new FavoritePlace(favoriteFolder, place, maxOrder + 1);
        FavoritePlace savedFavoritePlace = favoritePlaceRepository.save(favoritePlace);

        eventPublisher.publishEvent(FavoriteFolderUpdateEvent.of(favoriteFolderId, ActionType.PLACE_ADDED));

        return FavoritePlaceResponse.from(savedFavoritePlace);
    }

    @Transactional
    public List<FavoritePlaceResponse> batchCreate(Account account, Long favoriteFolderId, List<Long> placeIds) {
        if (placeIds == null) {
            throw new BadRequestException(ErrorTag.BAD_REQUEST);
        }

        FavoriteFolder favoriteFolder = getFavoriteFolderByIdWithLock(favoriteFolderId);
        favoriteFolderAccountService.validateMembership(account, favoriteFolder);

        List<Place> requestedPlaces = findPlacesByIdInRequestOrder(placeIds);
        List<Place> placesToAdd = filterPlacesToAdd(favoriteFolder, requestedPlaces);
        List<FavoritePlace> savedFavoritePlaces = saveFavoritePlaces(favoriteFolder, placesToAdd);

        if (!savedFavoritePlaces.isEmpty()) {
            eventPublisher.publishEvent(FavoriteFolderUpdateEvent.of(favoriteFolderId, ActionType.PLACE_ADDED));
        }

        return savedFavoritePlaces.stream()
                .map(FavoritePlaceResponse::from)
                .toList();
    }

    @Transactional
    public List<FavoritePlaceResponse> updateFavoriteFolders(Account account,
                                                             List<Long> favoriteFolderIds,
                                                             Long placeId) {
        List<Long> requestIds = favoriteFolderIds.stream().distinct().toList();
        List<FavoriteFolder> requestFolders = favoriteFolderRepository.findAllByIdInWithLock(requestIds);
        validateMultiFolder(account, requestFolders, requestIds);

        Place place = getPlaceById(placeId);
        List<FavoritePlace> existingPlaces = favoritePlaceRepository.findAllByPlaceAndAccount(place, account);

        Set<Long> affectedFolderIds = calculateAffectedFolderIds(existingPlaces, requestIds);

        deleteRemovedFavoritePlaces(existingPlaces, requestIds);
        List<FavoritePlace> createdPlaces = createFavoritePlaces(place, existingPlaces, requestFolders, requestIds);

        publishFolderUpdateEvents(affectedFolderIds);

        return convertToResultResponse(existingPlaces, createdPlaces, requestIds);
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
        FavoriteFolder favoriteFolder = getFavoriteFolderByIdWithLock(favoriteFolderId);
        favoriteFolderAccountService.validateMembership(account, favoriteFolder);

        List<Long> favoritePlaceIdsOrder = request.favoritePlaceIdsOrder();

        for (int index = 0; index < favoritePlaceIdsOrder.size(); index++) {
            Long favoritePlaceId = favoritePlaceIdsOrder.get(index);
            FavoritePlace favoritePlace = getFavoritePlaceById(favoritePlaceId);
            validateFavoritePlaceBelongsToFolder(favoritePlace, favoriteFolder);
            favoritePlace.updateFavoriteOrder(index + 1);
        }

        eventPublisher.publishEvent(FavoriteFolderUpdateEvent.of(favoriteFolderId, ActionType.PLACE_REORDERED));
    }

    @Transactional
    public void remove(Account account, Long favoriteFolderId, Long placeId) {
        FavoriteFolder favoriteFolder = getFavoriteFolderByIdWithLock(favoriteFolderId);
        Place place = getPlaceById(placeId);

        favoriteFolderAccountService.validateMembership(account, favoriteFolder);
        FavoritePlace favoritePlace = getByFavoriteFolderAndPlace(favoriteFolder, place);

        favoritePlaceRepository.delete(favoritePlace);

        eventPublisher.publishEvent(FavoriteFolderUpdateEvent.of(favoriteFolderId, ActionType.PLACE_DELETED));
    }

    private FavoriteFolder getFavoriteFolderById(Long favoriteFolderId) {
        return favoriteFolderRepository.findById(favoriteFolderId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.FAVORITE_FOLDER_NOT_FOUND));
    }

    private List<Place> findPlacesByIdInRequestOrder(List<Long> placeIds) {
        Map<Long, Place> placesById = placeRepository.findAllById(placeIds).stream()
                .collect(Collectors.toMap(Place::getId, place -> place));

        return placeIds.stream()
                .distinct()
                .map(placesById::get)
                .filter(Objects::nonNull)
                .toList();
    }

    private Place getPlaceById(Long placeId) {
        return placeRepository.findById(placeId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.PLACE_NOT_FOUND));
    }

    private FavoritePlace getFavoritePlaceById(Long favoritePlaceId) {
        return favoritePlaceRepository.findById(favoritePlaceId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.FAVORITE_PLACE_NOT_FOUND));
    }

    private Set<Long> calculateAffectedFolderIds(List<FavoritePlace> existingPlaces, List<Long> requestIds) {
        Set<Long> ids = existingPlaces.stream()
                .map(fp -> fp.getFavoriteFolder().getId())
                .collect(Collectors.toSet());
        ids.addAll(requestIds);
        return ids;
    }

    private void publishFolderUpdateEvents(Set<Long> folderIds) {
        folderIds.forEach(folderId ->
                eventPublisher.publishEvent(FavoriteFolderUpdateEvent.of(folderId, ActionType.FOLDER_PLACE_CHANGED))
        );
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
        requestedFavoriteFolders.forEach(folder -> favoriteFolderAccountService.validateMembership(account, folder));
    }

    private void validateDuplicated(FavoriteFolder favoriteFolder, Place place) {
        boolean isAlreadyFavorite = favoritePlaceRepository.existsByFavoriteFolderAndPlace(favoriteFolder, place);
        if (isAlreadyFavorite) {
            throw new ConflictException(ErrorTag.FAVORITE_PLACE_IN_FOLDER_CONFLICT);
        }
    }

    private List<Place> filterPlacesToAdd(FavoriteFolder favoriteFolder, List<Place> requestedPlaces) {
        // 이미 튜립에 추가되어 있는 장소들
        Set<Place> alreadyAddedPlaces = favoritePlaceRepository
                .findAllByFavoriteFolderAndPlaceIn(favoriteFolder, requestedPlaces)
                .stream()
                .map(FavoritePlace::getPlace)
                .collect(Collectors.toSet());

        return requestedPlaces.stream()
                .filter(place -> !alreadyAddedPlaces.contains(place))
                .toList();
    }

    private List<FavoritePlace> saveFavoritePlaces(FavoriteFolder favoriteFolder, List<Place> placesToAdd) {
        int nextOrder = favoritePlaceRepository.findMaxFavoriteOrderByFavoriteFolder(favoriteFolder).orElse(0) + 1;

        List<FavoritePlace> newFavoritePlaces = new ArrayList<>();
        for (Place place : placesToAdd) {
            newFavoritePlaces.add(new FavoritePlace(favoriteFolder, place, nextOrder));
            nextOrder++;
        }

        return favoritePlaceRepository.saveAll(newFavoritePlaces);
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

    private FavoriteFolder getFavoriteFolderByIdWithLock(Long favoriteFolderId) {
        return favoriteFolderRepository.findByIdWithLock(favoriteFolderId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.FAVORITE_FOLDER_NOT_FOUND));
    }
}
