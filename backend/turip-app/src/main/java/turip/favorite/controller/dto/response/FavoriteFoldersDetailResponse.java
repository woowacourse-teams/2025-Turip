package turip.favorite.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record FavoriteFoldersDetailResponse(
        @JsonProperty("turips")
        List<FavoriteFolderDetailResponse> favoriteFolders
) {

    public static FavoriteFoldersDetailResponse from(
            List<FavoriteFolderDetailResponse> favoriteFolders) {
        return new FavoriteFoldersDetailResponse(favoriteFolders);
    }
}
