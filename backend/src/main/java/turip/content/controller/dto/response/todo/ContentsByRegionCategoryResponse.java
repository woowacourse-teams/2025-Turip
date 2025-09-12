package turip.content.controller.dto.response.todo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ContentsByRegionCategoryResponse(
        @JsonProperty("contents")
        List<ContentDetailsResponse> contentDetailsResponse,
        boolean loadable
) {

    public static ContentsByRegionCategoryResponse of(
            List<ContentDetailsResponse> contents,
            boolean loadable
    ) {
        return new ContentsByRegionCategoryResponse(contents, loadable);
    }
} 
