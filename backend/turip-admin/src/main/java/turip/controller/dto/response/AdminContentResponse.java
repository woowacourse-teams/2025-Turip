package turip.controller.dto.response;

import java.time.LocalDate;
import turip.content.domain.Content;

public record AdminContentResponse(
        Long id,
        String title,
        String url,
        String cityName,
        String creatorChannelName,
        LocalDate uploadedDate
) {
    public static AdminContentResponse from(Content content) {
        return new AdminContentResponse(
                content.getId(),
                content.getTitle(),
                content.getUrl(),
                content.getCity().getName(),
                content.getCreator().getChannelName(),
                content.getUploadedDate()
        );
    }
}
