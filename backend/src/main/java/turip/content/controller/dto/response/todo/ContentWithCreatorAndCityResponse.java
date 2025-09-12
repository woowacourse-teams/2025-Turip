package turip.content.controller.dto.response.todo;

import java.time.LocalDate;
import turip.content.domain.Content;
import turip.creator.controller.dto.response.CreatorResponse;
import turip.region.controller.dto.response.CityNameResponse;

public record ContentWithCreatorAndCityResponse(
        Long id,
        CreatorResponse creator,
        String title,
        String url,
        LocalDate uploadedDate,
        CityNameResponse city
) {

    public static ContentWithCreatorAndCityResponse from(Content content) {
        return new ContentWithCreatorAndCityResponse(
                content.getId(),
                CreatorResponse.from(content.getCreator()),
                content.getTitle(),
                content.getUrl(),
                content.getUploadedDate(),
                CityNameResponse.from(content.getCity())
        );
    }
}
