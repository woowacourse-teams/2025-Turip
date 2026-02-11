package turip.favorite.stream.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import turip.favorite.stream.service.ActionType;

public record FolderUpdateStreamResponse(
        @JsonProperty("turipId") Long favoriteFolderId,
        @JsonProperty("action") ActionType actionType,
        LocalDateTime timestamp
) {

    public static FolderUpdateStreamResponse of(Long favoriteFolderId, ActionType actionType) {
        return new FolderUpdateStreamResponse(favoriteFolderId, actionType, LocalDateTime.now());
    }
}
