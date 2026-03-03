package turip.favorite.controller.dto.response;

import turip.account.domain.Account;
import turip.favorite.domain.FavoriteFolder;

public record FavoriteFolderDetailResponse(
        Long id,
        Long accountId,
        String name,
        boolean isDefault,
        int placeCount,
        int memberCount,
        boolean isShared
) {

    public static FavoriteFolderDetailResponse of(FavoriteFolder favoriteFolder, Account account,
                                                  int placeCount, int memberCount) {
        return new FavoriteFolderDetailResponse(favoriteFolder.getId(), account.getId(),
                favoriteFolder.getName(), favoriteFolder.isDefault(), placeCount, memberCount,
                favoriteFolder.isShared());
    }
}
