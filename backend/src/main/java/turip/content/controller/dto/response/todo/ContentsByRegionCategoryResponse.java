package turip.content.controller.dto.response.todo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ContentsByRegionCategoryResponse(
        @JsonProperty("contents")
        List<ContentDetailsByRegionCategoryResponse> contentDetailsByRegionCategoryResponse,
        boolean loadable,
        String regionCategoryName
) {

    public static ContentsByRegionCategoryResponse of(
            List<ContentDetailsByRegionCategoryResponse> contents,
            boolean loadable,
            String regionCategoryName
    ) {
        return new ContentsByRegionCategoryResponse(contents, loadable, regionCategoryName);
    }
} 
