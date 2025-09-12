package turip.content.controller.dto.response.todo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ContentsWithLoadable(
        @JsonProperty("contents")
        List<ContentDetailsResponse> contents,
        boolean loadable
) {

    public static ContentsWithLoadable of(List<ContentDetailsResponse> contents, boolean loadable) {
        return new ContentsWithLoadable(contents, loadable);
    }
}
