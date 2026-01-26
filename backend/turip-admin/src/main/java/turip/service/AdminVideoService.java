package turip.service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.common.exception.custom.NotFoundException;
import turip.controller.dto.response.AdminVideoResponse;

@Service
public class AdminVideoService {

    public static final String YOUTUBE_ITEMS_KEY = "items";
    public static final String YOUTUBE_SNIPPET_KEY = "snippet";
    private final RestClient restClient;
    private final String youtubeApiKey;

    public AdminVideoService(RestClient baseRestClient,
                             @Value("${youtube.api.key}") String youtubeApiKey,
                             @Value("${youtube.api.url}") String youtubeApiUrl) {
        this.restClient = baseRestClient.mutate()
                .baseUrl(youtubeApiUrl)
                .build();
        this.youtubeApiKey = youtubeApiKey;
    }

    public AdminVideoResponse getVideoData(String videoUrl) {
        String videoId = extractVideoId(videoUrl);
        if (videoId == null) {
            throw new BadRequestException(ErrorTag.INVALID_YOUTUBE_URL);
        }
        return fetchFromYoutubeApi(videoId);
    }

    private AdminVideoResponse fetchFromYoutubeApi(String videoId) {
        String response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("part", YOUTUBE_SNIPPET_KEY)
                        .queryParam("id", videoId)
                        .queryParam("key", youtubeApiKey)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, res) -> {
                    throw new BadRequestException(ErrorTag.YOUTUBE_API_REQUEST_FAILED);
                })
                .body(String.class);

        return parseJsonResponse(response, videoId);
    }

    private AdminVideoResponse parseJsonResponse(String response, String videoId) {
        JSONObject json = new JSONObject(response);

        if (!json.has(YOUTUBE_ITEMS_KEY) || json.getJSONArray(YOUTUBE_ITEMS_KEY).isEmpty()) {
            throw new NotFoundException(ErrorTag.YOUTUBE_VIDEO_NOT_FOUND);
        }

        JSONObject snippet = json.getJSONArray(YOUTUBE_ITEMS_KEY).getJSONObject(0).getJSONObject(YOUTUBE_SNIPPET_KEY);

        return AdminVideoResponse.of(
                videoId,
                snippet.getString("title"),
                snippet.getString("channelTitle"),
                convertToLocalDate(snippet.getString("publishedAt"))
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

    private LocalDate convertToLocalDate(String publishedAt) {
        return ZonedDateTime.parse(publishedAt).toLocalDate();
    }
}
