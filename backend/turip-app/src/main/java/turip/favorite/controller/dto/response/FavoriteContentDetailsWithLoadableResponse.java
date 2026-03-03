package turip.favorite.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import turip.favorite.service.dto.FavoriteContentWithLoadableResult;

public record FavoriteContentDetailsWithLoadableResponse(
        @JsonProperty("bookmarks") List<FavoriteContentDetailResponse> favoriteContents,
        boolean loadable
) {

    public static FavoriteContentDetailsWithLoadableResponse of(List<FavoriteContentDetailResponse> favoriteContents,
                                                                boolean loadable) {
        return new FavoriteContentDetailsWithLoadableResponse(favoriteContents, loadable);
    }

    public static FavoriteContentDetailsWithLoadableResponse from(FavoriteContentWithLoadableResult result) {
        return new FavoriteContentDetailsWithLoadableResponse(result.favoriteContents(), result.loadable());
    }
}
