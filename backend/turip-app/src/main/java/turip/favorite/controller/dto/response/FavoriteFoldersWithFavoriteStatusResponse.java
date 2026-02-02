package turip.favorite.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record FavoriteFoldersWithFavoriteStatusResponse(
        @JsonProperty("turips")
        List<FavoriteFolderWithFavoriteStatusResponse> favoriteFolders
) {

    public static FavoriteFoldersWithFavoriteStatusResponse from(
            List<FavoriteFolderWithFavoriteStatusResponse> favoriteFolders) {
        return new FavoriteFoldersWithFavoriteStatusResponse(favoriteFolders);
    }
}
