package turip.favorite.controller.dto.response;

import turip.account.domain.Account;
import turip.favorite.domain.FavoriteFolder;

public record FavoriteFolderResponse(
        Long id,
        Long accountId,
        String name,
        boolean isDefault
) {

    public static FavoriteFolderResponse of(FavoriteFolder favoriteFolder, Account account) {
        return new FavoriteFolderResponse(favoriteFolder.getId(), account.getId(),
                favoriteFolder.getName(), favoriteFolder.isDefault()
        );
    }
}
