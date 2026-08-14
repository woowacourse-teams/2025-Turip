package turip.controller.dto.response;

import java.time.LocalDateTime;
import turip.content.domain.ContentPending;

public record MyCollectContentResponse(
        Long id,
        String videoTitle,
        String cityName,
        String status,
        String rejectReason,
        LocalDateTime createdAt
) {

    public static MyCollectContentResponse from(ContentPending p) {
        return new MyCollectContentResponse(
                p.getId(),
                p.getContentData().video().title(),
                p.getContentData().cityName(),
                p.getStatus().name(),
                p.getRejectReason(),
                p.getCreatedAt()
        );
    }
}
