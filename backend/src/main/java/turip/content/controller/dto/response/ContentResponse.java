package turip.content.controller.dto.response;

import java.time.LocalDate;
import turip.region.controller.dto.response.CityNameResponse;
import turip.content.domain.Content;
import turip.creator.controller.dto.response.CreatorResponse;

public record ContentResponse(
        Long id,
        String title,
        String url,
        LocalDate uploadedDate,
        CityNameResponse city,
        CreatorResponse creator,
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
