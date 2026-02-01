package turip.favorite.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record FavoriteFoldersWithPlaceCountResponse(
        @JsonProperty("turips")
        List<FavoriteFolderWithPlaceCountResponse> favoriteFolders
) {

    public static FavoriteFoldersWithPlaceCountResponse from(
            List<FavoriteFolderWithPlaceCountResponse> favoriteFolders) {
        return new FavoriteFoldersWithPlaceCountResponse(favoriteFolders);
    }
}
