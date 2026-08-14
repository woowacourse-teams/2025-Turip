package turip.controller.dto.response;

import java.time.LocalDateTime;
import turip.content.domain.ContentPending;

public record PendingListResponse(
        Long id,
        String videoTitle,
        String channelName,
        String collectorNickname,
        String validatorNickname,
        LocalDateTime createdAt
) {

    public static PendingListResponse from(ContentPending p) {
        return new PendingListResponse(
                p.getId(),
                p.getContentData().video().title(),
                p.getContentData().video().channelName(),
                p.getCollectorAccount().getNickname(),
                p.getValidatorAccount() != null ? p.getValidatorAccount().getNickname() : null,
                p.getCreatedAt()
        );
    }
}
