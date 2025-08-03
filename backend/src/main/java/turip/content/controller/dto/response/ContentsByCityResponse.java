package turip.content.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ContentsByCityResponse(
        @JsonProperty("contents")
        List<ContentDetailsByCityResponse> contentDetailsByCityResponse,
        boolean loadable
) {

    public static ContentsByCityResponse of(List<ContentDetailsByCityResponse> contents, boolean loadable) {
        return new ContentsByCityResponse(contents, loadable);
    }
}
