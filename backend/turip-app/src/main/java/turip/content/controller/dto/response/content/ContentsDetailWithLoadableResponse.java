package turip.content.controller.dto.response.content;

import java.util.List;
import turip.favorite.service.dto.FavoriteContentWithLoadableResult;

public record ContentsDetailWithLoadableResponse(
        List<ContentDetailResponse> contents,
        boolean loadable
) {

    public static ContentsDetailWithLoadableResponse of(List<ContentDetailResponse> contents, boolean loadable) {
        return new ContentsDetailWithLoadableResponse(contents, loadable);
    }

    public static ContentsDetailWithLoadableResponse from(FavoriteContentWithLoadableResult result) {
        List<ContentDetailResponse> contents = result.favoriteContents().stream()
                .map(ContentDetailResponse::from)
                .toList();
        boolean loadable = result.loadable();
        return new ContentsDetailWithLoadableResponse(contents, loadable);
    }
}
