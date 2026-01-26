package turip.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.infrastructure.client.dto.YoutubeVideoApiResponse;
import turip.infrastructure.client.dto.YoutubeVideoSearchResponse;

@Component
public class YoutubeVideoSearchClient {

    private final RestClient restClient;
    private final String youtubeApiKey;

    public YoutubeVideoSearchClient(RestClient baseRestClient,
                                    @Value("${youtube.api.key}") String youtubeApiKey,
                                    @Value("${youtube.api.url}") String youtubeApiUrl) {
        this.restClient = baseRestClient.mutate()
                .baseUrl(youtubeApiUrl)
                .build();
        this.youtubeApiKey = youtubeApiKey;
    }

    public YoutubeVideoSearchResponse searchByVideoId(String videoId) {
        YoutubeVideoApiResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("part", "snippet")
                        .queryParam("id", videoId)
                        .queryParam("key", youtubeApiKey)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, res) -> {
                    throw new BadRequestException(ErrorTag.YOUTUBE_API_REQUEST_FAILED);
                })
                .body(YoutubeVideoApiResponse.class);

        return response.toVideoSearchResponse(videoId);
    }
}
