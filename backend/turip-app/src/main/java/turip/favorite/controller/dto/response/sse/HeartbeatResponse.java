package turip.favorite.controller.dto.response.sse;

import java.time.LocalDateTime;

public record HeartbeatResponse(LocalDateTime timestamp) {

    public static HeartbeatResponse create() {
        return new HeartbeatResponse(LocalDateTime.now());
    }
}
