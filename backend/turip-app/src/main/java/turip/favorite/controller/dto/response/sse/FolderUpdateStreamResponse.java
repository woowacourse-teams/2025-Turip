package turip.favorite.controller.dto.response.sse;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import turip.favorite.service.ActionType;

public record FolderUpdateStreamResponse(
        @JsonProperty("folderId") Long favoriteFolderId,
        @JsonProperty("action") ActionType actionType,
        LocalDateTime timestamp
) {

    public static FolderUpdateStreamResponse of(Long favoriteFolderId, ActionType actionType) {
        return new FolderUpdateStreamResponse(favoriteFolderId, actionType, LocalDateTime.now());
    }
}
