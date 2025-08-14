package turip.favoritefolder.controller.dto.response;

import turip.favoritefolder.domain.FavoriteFolder;

public record FavoriteFolderWithPlaceCountResponse(
        Long id,
        Long memberId,
        String name,
        boolean isDefault,
        int placeCount
) {

    public static FavoriteFolderWithPlaceCountResponse of(FavoriteFolder favoriteFolder, int placeCount) {
        return new FavoriteFolderWithPlaceCountResponse(favoriteFolder.getId(), favoriteFolder.getMember().getId(),
                favoriteFolder.getName(), favoriteFolder.isDefault(), placeCount);
    }
}
