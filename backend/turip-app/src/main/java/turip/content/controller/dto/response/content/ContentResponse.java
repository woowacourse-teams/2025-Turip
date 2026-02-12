package turip.content.controller.dto.response.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import turip.content.domain.Content;
import turip.creator.controller.dto.response.CreatorResponse;
import turip.region.controller.dto.response.CityNameResponse;

public record ContentResponse(
        Long id,
        String title,
        String url,
        LocalDate uploadedDate,
        CityNameResponse city,
        CreatorResponse creator,
        @JsonProperty("isBookmarked")
        boolean isFavorite
) {
    public static ContentResponse of(Content content, boolean isFavorite) {
        return new ContentResponse(
                content.getId(),
                content.getTitle(),
                content.getUrl(),
                content.getUploadedDate(),
                CityNameResponse.from(content.getCity()),
                CreatorResponse.from(content.getCreator()),
                isFavorite
        );
    }
}
