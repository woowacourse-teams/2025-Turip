package turip.content.controller.dto.response;

import java.time.LocalDate;
import turip.content.domain.Content;
import turip.creator.controller.dto.response.CreatorResponse;

public record ContentByCityResponse(
        Long id,
        CreatorResponse creator,
        String title,
        String url,
        LocalDate uploadedDate,
        String city
) {

    public static ContentByCityResponse from(Content content) {
        return new ContentByCityResponse(
                content.getId(),
                CreatorResponse.from(content.getCreator()),
                content.getTitle(),
                content.getUrl(),
                content.getUploadedDate(),
                content.getCity().getName()
        );
    }
} 
