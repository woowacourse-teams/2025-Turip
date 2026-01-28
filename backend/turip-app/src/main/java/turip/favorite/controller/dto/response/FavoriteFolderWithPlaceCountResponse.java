package turip.favorite.controller.dto.response;

import turip.favorite.domain.FavoriteFolder;

public record FavoriteFolderWithPlaceCountResponse(
        Long id,
        Long accountId,
        String name,
        boolean isDefault,
        int placeCount
) {

    public static FavoriteFolderWithPlaceCountResponse of(FavoriteFolder favoriteFolder, int placeCount) {
        return new FavoriteFolderWithPlaceCountResponse(favoriteFolder.getId(), favoriteFolder.getAccount().getId(),
                favoriteFolder.getName(), favoriteFolder.isDefault(), placeCount);
    }
}
