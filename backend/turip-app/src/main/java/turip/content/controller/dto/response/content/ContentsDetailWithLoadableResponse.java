package turip.content.controller.dto.response.content;

import java.util.List;

public record ContentsDetailWithLoadableResponse(
        List<ContentDetailResponse> contents,
        boolean loadable
) {

    public static ContentsDetailWithLoadableResponse of(List<ContentDetailResponse> contents, boolean loadable) {
        return new ContentsDetailWithLoadableResponse(contents, loadable);
    }
}
