package turip.content.controller.dto.response.todo;

import java.util.List;

public record ContentsWithLoadableResponse(
        List<ContentDetailResponse> contents,
        boolean loadable
) {

    public static ContentsWithLoadableResponse of(List<ContentDetailResponse> contents, boolean loadable) {
        return new ContentsWithLoadableResponse(contents, loadable);
    }
}
