package turip.content.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ContentSearchResponse(
        @JsonProperty("contents")
        List<ContentSearchResultResponse> contentDetailsByRegionResponse,
        boolean loadable
) {

    public static ContentSearchResponse of(List<ContentSearchResultResponse> contents, boolean loadable) {
        return new ContentSearchResponse(contents, loadable);
    }
}
