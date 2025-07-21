package turip.content.controller.dto.response;

import java.util.List;

public record ContentListByRegionResponse(
        List<ContentDetailsByRegionResponse> contentDetailsByRegionResponse,
        boolean loadable
) {

    public static ContentListByRegionResponse of(List<ContentDetailsByRegionResponse> contents, boolean loadable) {
        return new ContentListByRegionResponse(contents, loadable);
    }
}
