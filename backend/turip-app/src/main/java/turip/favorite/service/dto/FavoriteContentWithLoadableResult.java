package turip.favorite.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import turip.favorite.controller.dto.response.FavoriteContentDetailResponse;

public record FavoriteContentWithLoadableResult(
        @JsonProperty("bookmarks") List<FavoriteContentDetailResponse> favoriteContents,
        boolean loadable
) {

    public static FavoriteContentWithLoadableResult of(List<FavoriteContentDetailResponse> favoriteContents,
                                                       boolean loadable) {
        return new FavoriteContentWithLoadableResult(favoriteContents, loadable);
    }
}
