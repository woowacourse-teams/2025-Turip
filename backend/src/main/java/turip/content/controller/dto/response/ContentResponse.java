package turip.content.controller.dto.response;

import java.time.LocalDate;
import turip.content.domain.Content;

public record ContentResponse(
        Long id,
        Long creatorId,
        Long cityId,
        String title,
        String url,
        LocalDate uploadedDate,
        boolean isFavorite
) {
    public static ContentResponse of(Content content, boolean isFavorite) {
        return new ContentResponse(
                content.getId(),
                content.getCreator().getId(),
                content.getCity().getId(),
                content.getTitle(),
                content.getUrl(),
                content.getUploadedDate(),
                isFavorite
        );
    }
}
