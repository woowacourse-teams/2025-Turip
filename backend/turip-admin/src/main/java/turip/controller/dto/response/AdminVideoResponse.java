package turip.controller.dto.response;

import java.time.LocalDate;

public record AdminVideoResponse(
        String videoId,
        String videoTitle,
        String channelName,
        LocalDate uploadDate
) {

    public static AdminVideoResponse of(
            String videoId,
            String videoTitle,
            String channelName,
            LocalDate uploadDate
    ) {
        return new AdminVideoResponse(videoId, videoTitle, channelName, uploadDate);
    }
}
