package turip.favoriteplace.controller.dto.response;

import turip.favoriteplace.domain.FavoritePlace;

public record FavoritePlaceResponse(
        Long id,
        Long favoriteFolderId,
        Long placeId
) {

    public static FavoritePlaceResponse from(FavoritePlace favoritePlace) {
        return new FavoritePlaceResponse(favoritePlace.getId(), favoritePlace.getFavoriteFolder().getId(),
                favoritePlace.getPlace().getId());
    }
}
