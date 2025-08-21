package turip.favorite.controller.dto.response;

import java.util.List;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.domain.FavoritePlace;
import turip.place.controller.dto.response.PlaceResponse;

public record FavoriteFolderWithFavoriteStatusResponse(
        Long id,
        Long memberId,
        String name,
        boolean isDefault,
        boolean isFavoritePlace
) {

    public static FavoriteFolderWithFavoriteStatusResponse of(FavoriteFolder favoriteFolder, boolean isFavoritePlace) {
        return new FavoriteFolderWithFavoriteStatusResponse(favoriteFolder.getId(), favoriteFolder.getMember().getId(),
                favoriteFolder.getName(), favoriteFolder.isDefault(), isFavoritePlace);
    }

    public static record FavoritePlaceResponse(
            Long id,
            Long favoriteFolderId,
            Long placeId
    ) {

        public static FavoritePlaceResponse from(FavoritePlace favoritePlace) {
            return new FavoritePlaceResponse(favoritePlace.getId(), favoritePlace.getFavoriteFolder().getId(),
                    favoritePlace.getPlace().getId());
        }
    }

    public static record FavoritePlacesWithDetailPlaceInformationResponse(
            List<FavoritePlaceWithDetailPlaceInformationResponse> favoritePlaces, Integer favoritePlaceCount) {

        public static FavoritePlacesWithDetailPlaceInformationResponse from(
                List<FavoritePlaceWithDetailPlaceInformationResponse> favoritePlaces) {
            return new FavoritePlacesWithDetailPlaceInformationResponse(favoritePlaces, favoritePlaces.size());
        }
    }

    public static record FavoritePlaceWithDetailPlaceInformationResponse(Long id, PlaceResponse place) {

        public static FavoritePlaceWithDetailPlaceInformationResponse from(FavoritePlace favoritePlace) {
            return new FavoritePlaceWithDetailPlaceInformationResponse(favoritePlace.getId(),
                    PlaceResponse.from(favoritePlace.getPlace()));
        }
    }
}
