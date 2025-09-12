package turip.content.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import turip.content.controller.dto.response.todo.ContentDetailsResponse;

public record ContentSearchResponse(
        @JsonProperty("contents")
        List<ContentDetailsResponse> contents,
        boolean loadable
) {

    public static ContentSearchResponse of(List<ContentDetailsResponse> contents, boolean loadable) {
        return new ContentSearchResponse(contents, loadable);
    }
}
