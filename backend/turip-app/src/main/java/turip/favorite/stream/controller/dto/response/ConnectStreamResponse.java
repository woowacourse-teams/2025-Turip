package turip.favorite.stream.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record ConnectStreamResponse(@JsonProperty("turipId") Long favoriteFolderId, LocalDateTime timestamp) {

    public static ConnectStreamResponse from(Long favoriteFolderId) {
        return new ConnectStreamResponse(favoriteFolderId, LocalDateTime.now());
    }
}
