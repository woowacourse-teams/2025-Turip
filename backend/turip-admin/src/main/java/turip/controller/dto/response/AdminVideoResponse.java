package turip.controller.dto.response;

import java.time.LocalDate;

public record AdminVideoResponse(
        String videoId,
        String videoTitle,
        String channelId,
        String channelName,
        LocalDate uploadDate,
        boolean isExisting
) {

    public static AdminVideoResponse of(
            String videoId,
            String videoTitle,
            String channelId,
            String channelName,
            LocalDate uploadDate,
            boolean isExisting
    ) {
        return new AdminVideoResponse(videoId, videoTitle, channelId, channelName, uploadDate, isExisting);
    }
}
