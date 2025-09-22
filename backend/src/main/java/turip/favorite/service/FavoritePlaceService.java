package turip.favorite.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.ConflictException;
import turip.common.exception.custom.ForbiddenException;
import turip.common.exception.custom.NotFoundException;
import turip.favorite.controller.dto.request.FavoritePlaceOrderRequest;
import turip.favorite.controller.dto.response.FavoriteFolderWithFavoriteStatusResponse.FavoritePlaceResponse;
import turip.favorite.controller.dto.response.FavoriteFolderWithFavoriteStatusResponse.FavoritePlaceWithDetailPlaceInformationResponse;
import turip.favorite.controller.dto.response.FavoriteFolderWithFavoriteStatusResponse.FavoritePlacesWithDetailPlaceInformationResponse;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.domain.FavoritePlace;
import turip.favorite.repository.FavoriteFolderRepository;
import turip.favorite.repository.FavoritePlaceRepository;
import turip.member.domain.Member;
import turip.place.domain.Place;
import turip.place.repository.PlaceRepository;

@Service
@RequiredArgsConstructor
public class FavoritePlaceService {

    private final FavoritePlaceRepository favoritePlaceRepository;
    private final FavoriteFolderRepository favoriteFolderRepository;
    private final PlaceRepository placeRepository;

    @Transactional
    public FavoritePlaceResponse create(Member member, Long favoriteFolderId, Long placeId) {
        FavoriteFolder favoriteFolder = getFavoriteFolderById(favoriteFolderId);
        Place place = getPlaceById(placeId);

        validateOwnership(member, favoriteFolder);
        validateDuplicated(favoriteFolder, place);

        int favoritePlaceTotalCount = favoritePlaceRepository.countByFavoriteFolder(favoriteFolder);

        FavoritePlace favoritePlace = new FavoritePlace(favoriteFolder, place, favoritePlaceTotalCount + 1);
        FavoritePlace savedFavoritePlace = favoritePlaceRepository.save(favoritePlace);

        return FavoritePlaceResponse.from(savedFavoritePlace);
    }

    public FavoritePlacesWithDetailPlaceInformationResponse findAllByFolder(Long favoriteFolderId) {
        FavoriteFolder favoriteFolder = getFavoriteFolderById(favoriteFolderId);

        List<FavoritePlaceWithDetailPlaceInformationResponse> favoritePlaces = favoritePlaceRepository.findAllByFavoriteFolderOrderByFavoriteOrderAsc(
                        favoriteFolder).stream()
                .map(FavoritePlaceWithDetailPlaceInformationResponse::from)
                .toList();

        return FavoritePlacesWithDetailPlaceInformationResponse.from(favoritePlaces);
    }

    @Transactional
    public void updatePlaceOrder(Member member, Long favoriteFolderId,
                                 FavoritePlaceOrderRequest request) {
        FavoriteFolder favoriteFolder = getFavoriteFolderById(favoriteFolderId);
        validateOwnership(member, favoriteFolder);

        List<Long> favoritePlaceIdsOrder = request.favoritePlaceIdsOrder();

        for (int index = 0; index < favoritePlaceIdsOrder.size(); index++) {
            Long favoritePlaceId = favoritePlaceIdsOrder.get(index);
            FavoritePlace favoritePlace = getFavoritePlaceById(favoritePlaceId);
            validateFavoritePlaceBelongsToFolder(favoritePlace, favoriteFolder);
            favoritePlace.updateFavoriteOrder(index + 1);
        }
    }

    @Transactional
    public void remove(Member member, Long favoriteFolderId, Long placeId) {
        FavoriteFolder favoriteFolder = getFavoriteFolderById(favoriteFolderId);
        Place place = getPlaceById(placeId);

        validateOwnership(member, favoriteFolder);
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

    private void validateOwnership(Member requestMember, FavoriteFolder favoriteFolder) {
        if (!favoriteFolder.isOwner(requestMember)) {
            throw new ForbiddenException(ErrorTag.FORBIDDEN);
        }
    }

    private void validateDuplicated(FavoriteFolder favoriteFolder, Place place) {
        boolean isAlreadyFavorite = favoritePlaceRepository.existsByFavoriteFolderAndPlace(favoriteFolder, place);
        if (isAlreadyFavorite) {
            throw new ConflictException(ErrorTag.FAVORITE_PLACE_IN_FOLDER_CONFLICT);
        }
    }

    private void validateFavoritePlaceBelongsToFolder(FavoritePlace favoritePlace, FavoriteFolder favoriteFolder) {
        if (!favoritePlace.getFavoriteFolder().equals(favoriteFolder)) {
            throw new ForbiddenException(ErrorTag.FORBIDDEN);
        }
    }

    private FavoritePlace getByFavoriteFolderAndPlace(FavoriteFolder favoriteFolder, Place place) {
        return favoritePlaceRepository.findByFavoriteFolderAndPlace(favoriteFolder, place)
                .orElseThrow(() -> new NotFoundException(ErrorTag.FAVORITE_PLACE_NOT_FOUND));
    }
}
