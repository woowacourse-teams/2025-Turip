package turip.controller.dto.response;

import java.time.LocalDateTime;

public record MyCollectContentResponse(
        Long id,
        String videoTitle,
        String cityName,
        String status,
        String rejectReason,
        LocalDateTime createdAt
) {
}
