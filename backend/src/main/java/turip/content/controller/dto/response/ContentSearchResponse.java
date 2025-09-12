package turip.content.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import turip.content.controller.dto.response.todo.ContentWithTripInfo;

public record ContentSearchResponse(
        @JsonProperty("contents")
        List<ContentWithTripInfo> contentDetailsByRegionResponse,
        boolean loadable
) {

    public static ContentSearchResponse of(List<ContentWithTripInfo> contents, boolean loadable) {
        return new ContentSearchResponse(contents, loadable);
    }
}
