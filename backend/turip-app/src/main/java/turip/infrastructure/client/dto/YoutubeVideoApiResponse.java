package turip.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import java.util.List;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.NotFoundException;

public record YoutubeVideoApiResponse(List<Item> items) {

    public YoutubeVideoSearchResponse toVideoSearchResponse(String videoId) {
        if (items == null || items.isEmpty()) {
            throw new NotFoundException(ErrorTag.YOUTUBE_VIDEO_NOT_FOUND);
        }

        Snippet snippet = items.getFirst().snippet();
        return YoutubeVideoSearchResponse.of(
                videoId,
                snippet.title(),
                snippet.channelId(),
                snippet.channelTitle(),
                snippet.publishedAt().toLocalDate()
        );
    }

    private record Item(Snippet snippet) {
    }

    private record Snippet(
            String title,
            @JsonProperty("channelId") String channelId,
            @JsonProperty("channelTitle") String channelTitle,
            @JsonProperty("publishedAt") ZonedDateTime publishedAt
    ) {
    }
}
