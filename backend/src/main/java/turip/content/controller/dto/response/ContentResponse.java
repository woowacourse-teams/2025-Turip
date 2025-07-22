package turip.content.controller.dto.response;

import java.time.LocalDate;
import turip.content.domain.Content;

public record ContentResponse(
        Long id,
        Long creatorId,
        Long regionId,
        String title,
        String url,
        LocalDate uploadedDate
) {
    public static ContentResponse of(Content content) {
        return new ContentResponse(
                content.getId(),
                content.getCreator().getId(),
                content.getRegion().getId(),
                content.getTitle(),
                content.getUrl(),
                content.getUploadedDate()
        );
    }
}
