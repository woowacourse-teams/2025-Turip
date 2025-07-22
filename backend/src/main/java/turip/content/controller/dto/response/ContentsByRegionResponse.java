package turip.content.controller.dto.response;

import java.util.List;

public record ContentsByRegionResponse(
        List<ContentDetailsByRegionResponse> contentDetailsByRegionResponse,
        boolean loadable
) {

    public static ContentsByRegionResponse of(List<ContentDetailsByRegionResponse> contents, boolean loadable) {
        return new ContentsByRegionResponse(contents, loadable);
    }
}
