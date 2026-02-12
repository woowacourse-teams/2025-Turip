package turip.favorite.controller.dto.response;

import turip.account.domain.Account;
import turip.favorite.domain.FavoriteFolder;

public record FavoriteFolderWithPlaceCountResponse(
        Long id,
        Long accountId,
        String name,
        boolean isDefault,
        int placeCount
) {

    public static FavoriteFolderWithPlaceCountResponse of(FavoriteFolder favoriteFolder, Account account,
                                                          int placeCount) {
        return new FavoriteFolderWithPlaceCountResponse(favoriteFolder.getId(), account.getId(),
                favoriteFolder.getName(), favoriteFolder.isDefault(), placeCount);
    }
}
