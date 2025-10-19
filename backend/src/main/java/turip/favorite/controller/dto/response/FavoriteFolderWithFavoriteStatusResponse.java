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
        return new FavoriteFolderWithFavoriteStatusResponse(
                favoriteFolder.getId(),
                favoriteFolder.getMember().getId(),
                favoriteFolder.getName(),
                favoriteFolder.isDefault(),
                isFavoritePlace
        );
    }

    public record FavoritePlaceResponse(
            Long id,
            Long favoriteFolderId,
            Long placeId
    ) {

        public static FavoritePlaceResponse from(FavoritePlace favoritePlace) {
            return new FavoritePlaceResponse(
                    favoritePlace.getId(),
                    favoritePlace.getFavoriteFolder().getId(),
                    favoritePlace.getPlace().getId()
            );
        }
    }

    public record FavoritePlacesWithPlaceDetailResponse(
            List<FavoritePlaceWithPlaceDetailResponse> favoritePlaces, Integer favoritePlaceCount) {

        public static FavoritePlacesWithPlaceDetailResponse from(
                List<FavoritePlaceWithPlaceDetailResponse> favoritePlaces) {
            return new FavoritePlacesWithPlaceDetailResponse(favoritePlaces, favoritePlaces.size());
        }
    }

    public record FavoritePlaceWithPlaceDetailResponse(
            Long id,
            PlaceResponse place,
            Integer favoriteOrder
    ) {

        public static FavoritePlaceWithPlaceDetailResponse from(FavoritePlace favoritePlace) {
            return new FavoritePlaceWithPlaceDetailResponse(
                    favoritePlace.getId(),
                    PlaceResponse.from(favoritePlace.getPlace()),
                    favoritePlace.getFavoriteOrder()
            );
        }
    }
}
