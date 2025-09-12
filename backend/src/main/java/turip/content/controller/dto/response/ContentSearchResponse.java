package turip.content.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import turip.content.controller.dto.response.todo.ContentWithTripInfoResponse;

public record ContentSearchResponse(
        @JsonProperty("contents")
        List<ContentWithTripInfoResponse> contentDetailsByRegionResponse,
        boolean loadable
) {

    public static ContentSearchResponse of(List<ContentWithTripInfoResponse> contents, boolean loadable) {
        return new ContentSearchResponse(contents, loadable);
    }
}
