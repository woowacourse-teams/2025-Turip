package turip.controller.dto.response;

import java.time.LocalDateTime;

public record PendingListResponse(
        Long id,
        String videoTitle,
        String channelName,
        String collectorNickname,
        LocalDateTime createdAt
) {
}
