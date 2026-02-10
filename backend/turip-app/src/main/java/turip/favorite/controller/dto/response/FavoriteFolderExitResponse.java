package turip.favorite.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FavoriteFolderExitResponse(
        Long id,
        @JsonProperty("turipId") Long favoriteFolderId,
        boolean isDeleted
) {

    public static FavoriteFolderExitResponse of(Long id, Long favoriteFolderId, boolean isDeleted) {
        return new FavoriteFolderExitResponse(id, favoriteFolderId, isDeleted);
    }
}
