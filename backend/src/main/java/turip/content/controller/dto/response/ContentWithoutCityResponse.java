package turip.content.controller.dto.response;

import java.time.LocalDate;
import turip.content.domain.Content;
import turip.creator.controller.dto.response.CreatorResponse;

public record ContentWithoutCityResponse(
        Long id,
        CreatorResponse creator,
        String title,
        String url,
        LocalDate uploadedDate
) {

    public static ContentWithoutCityResponse from(Content content) {
        return new ContentWithoutCityResponse(
                content.getId(),
                CreatorResponse.from(content.getCreator()),
                content.getTitle(),
                content.getUrl(),
                content.getUploadedDate()
        );
    }
}
