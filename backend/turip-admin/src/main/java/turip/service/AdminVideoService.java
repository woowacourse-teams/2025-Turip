package turip.service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.controller.dto.response.AdminVideoResponse;
import turip.infrastructure.client.YoutubeVideoSearchClient;
import turip.infrastructure.client.dto.YoutubeVideoSearchResponse;

@Service
@RequiredArgsConstructor
public class AdminVideoService {

    private final YoutubeVideoSearchClient youtubeVideoSearchClient;

    public AdminVideoResponse getVideoData(String videoUrl) {
        String videoId = extractVideoId(videoUrl);
        if (videoId == null) {
            throw new BadRequestException(ErrorTag.INVALID_YOUTUBE_URL);
        }

        YoutubeVideoSearchResponse searchResponse = youtubeVideoSearchClient.searchByVideoId(videoId);

        return AdminVideoResponse.of(
                searchResponse.videoId(),
                searchResponse.title(),
                searchResponse.channelName(),
                searchResponse.uploadedDate()
        );
    }

    private String extractVideoId(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }
        try {
            String decodedInput = URLDecoder.decode(input, StandardCharsets.UTF_8).trim();
            if (decodedInput.matches("^[A-Za-z0-9_-]{11}$")) {
                return decodedInput;
            }

            String[] patterns = {
                    "youtu\\.be/([A-Za-z0-9_-]{11})",
                    "v=([A-Za-z0-9_-]{11})",
                    "embed/([A-Za-z0-9_-]{11})",
                    "shorts/([A-Za-z0-9_-]{11})"
            };

            for (String p : patterns) {
                Matcher m = Pattern.compile(p).matcher(decodedInput);
                if (m.find()) {
                    return m.group(1);
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
