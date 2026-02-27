package turip.favorite.stream.controller.dto.response;

import java.time.LocalDateTime;

public record HeartbeatStreamResponse(LocalDateTime timestamp) {

    public static HeartbeatStreamResponse create() {
        return new HeartbeatStreamResponse(LocalDateTime.now());
    }
}
