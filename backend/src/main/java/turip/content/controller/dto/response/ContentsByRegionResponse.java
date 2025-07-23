package turip.content.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ContentsByRegionResponse(
        @JsonProperty("contents")
        List<ContentDetailsByRegionResponse> contentDetailsByRegionResponse,
        boolean loadable
) {

    public static ContentsByRegionResponse of(List<ContentDetailsByRegionResponse> contents, boolean loadable) {
        return new ContentsByRegionResponse(contents, loadable);
    }
}
