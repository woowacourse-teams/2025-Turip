package turip.content.controller.dto.response;

import java.time.LocalDate;
import turip.city.controller.dto.response.CityNameResponse;
import turip.content.domain.Content;
import turip.creator.controller.dto.response.CreatorResponse;

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
