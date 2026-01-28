package turip.favorite.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.domain.FavoritePlace;
import turip.place.controller.dto.response.PlaceResponse;

public record FavoriteFolderWithFavoriteStatusResponse(
        Long id,
        Long accountId,
        String name,
        boolean isDefault,
        @JsonProperty("isTuripPlace")
        boolean isFavoritePlace
) {

    public static FavoriteFolderWithFavoriteStatusResponse of(FavoriteFolder favoriteFolder, boolean isFavoritePlace) {
        return new FavoriteFolderWithFavoriteStatusResponse(
                favoriteFolder.getId(),
                favoriteFolder.getAccount().getId(),
                favoriteFolder.getName(),
                favoriteFolder.isDefault(),
                isFavoritePlace
        );
    }

    public record FavoritePlaceResponse(
            Long id,
            @JsonProperty("turipId")
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
            @JsonProperty("turipPlaces")
            List<FavoritePlaceWithPlaceDetailResponse> favoritePlaces,
            @JsonProperty("turipPlaceCount")
            Integer favoritePlaceCount) {

        public static FavoritePlacesWithPlaceDetailResponse from(
                List<FavoritePlaceWithPlaceDetailResponse> favoritePlaces) {
            return new FavoritePlacesWithPlaceDetailResponse(favoritePlaces, favoritePlaces.size());
        }
    }

    public record FavoritePlaceWithPlaceDetailResponse(
            Long id,
            PlaceResponse place,
            @JsonProperty("turipPlaceOrder")
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
