package turip.favorite.controller.dto.response;

import java.util.List;

public record FavoriteFoldersWithPlaceCountResponse(List<FavoriteFolderWithPlaceCountResponse> favoriteFolders) {

    public static FavoriteFoldersWithPlaceCountResponse from(
            List<FavoriteFolderWithPlaceCountResponse> favoriteFolders) {
        return new FavoriteFoldersWithPlaceCountResponse(favoriteFolders);
    }
}
