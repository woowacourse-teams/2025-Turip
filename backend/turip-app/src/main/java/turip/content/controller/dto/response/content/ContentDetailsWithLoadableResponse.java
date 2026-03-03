package turip.content.controller.dto.response.content;

import java.util.List;
import turip.favorite.service.dto.FavoriteContentWithLoadableResult;

public record ContentDetailsWithLoadableResponse(
        List<ContentDetailResponse> contents,
        boolean loadable
) {

    public static ContentDetailsWithLoadableResponse of(List<ContentDetailResponse> contents, boolean loadable) {
        return new ContentDetailsWithLoadableResponse(contents, loadable);
    }

    public static ContentDetailsWithLoadableResponse from(FavoriteContentWithLoadableResult result) {
        List<ContentDetailResponse> contents = result.favoriteContents().stream()
                .map(ContentDetailResponse::from)
                .toList();
        boolean loadable = result.loadable();
        return new ContentDetailsWithLoadableResponse(contents, loadable);
    }
}
