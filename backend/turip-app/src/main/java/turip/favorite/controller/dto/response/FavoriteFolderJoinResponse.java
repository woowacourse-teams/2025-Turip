package turip.favorite.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.domain.FavoriteFolderAccount;

public record FavoriteFolderJoinResponse(
        Long id,
        @JsonProperty("turipId") Long favoriteFolderId,
        boolean isShared,
        Long accountId
) {

    public static FavoriteFolderJoinResponse from(FavoriteFolderAccount favoriteFolderAccount) {
        FavoriteFolder favoriteFolder = favoriteFolderAccount.getFavoriteFolder();
        return new FavoriteFolderJoinResponse(
                favoriteFolderAccount.getId(),
                favoriteFolder.getId(),
                favoriteFolder.isShared(),
                favoriteFolderAccount.getAccount().getId()
        );
    }
}
