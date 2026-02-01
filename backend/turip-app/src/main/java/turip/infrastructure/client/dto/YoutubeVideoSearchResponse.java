package turip.infrastructure.client.dto;

import java.time.LocalDate;

public record YoutubeVideoSearchResponse(
        String videoId,
        String title,
        String channelId,
        String channelName,
        LocalDate uploadedDate
) {

    public static YoutubeVideoSearchResponse of(
            String videoId,
            String videoTitle,
            String channelId,
            String channelName,
            LocalDate uploadedDate
    ) {
        return new YoutubeVideoSearchResponse(videoId, videoTitle, channelId, channelName, uploadedDate);
    }
}
