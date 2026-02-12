package turip.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import turip.common.configuration.RestClientConfiguration;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.common.exception.custom.NotFoundException;
import turip.common.log.ExternalApiLoggingInterceptor;
import turip.infrastructure.client.dto.YoutubeVideoSearchResponse;

@ActiveProfiles("test")
@RestClientTest(YoutubeVideoSearchClient.class)
@Import({RestClientConfiguration.class, ExternalApiLoggingInterceptor.class})
class YoutubeVideoSearchClientTest {

    @Autowired
    private YoutubeVideoSearchClient youtubeVideoSearchClient;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @Value("${youtube.api.url}")
    private String youtubeApiUrl;

    @Value("${youtube.api.key}")
    private String youtubeApiKey;

    @Test
    @DisplayName("유효한 videoId로 영상 정보를 성공적으로 가져온다")
    void searchByVideoId_withValidVideoId_returnsVideoResponse() {
        // given
        String videoId = "dQw4w9WgXcQ";
        String mockResponse = """
                {
                  "items": [
                    {
                      "snippet": {
                        "publishedAt": "2025-10-25T06:57:33Z",
                        "title": "하루 여행 vlog",
                        "channelTitle": "하루"
                      }
                    }
                  ]
                }""";

        String expectedUrl = youtubeApiUrl + "?part=snippet&id=" + videoId + "&key=" + youtubeApiKey;
        mockRestServiceServer.expect(requestTo(expectedUrl))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        // when
        YoutubeVideoSearchResponse response = youtubeVideoSearchClient.searchByVideoId(videoId);

        // then
        mockRestServiceServer.verify();
        assertThat(response.videoId()).isEqualTo(videoId);
        assertThat(response.title()).isEqualTo("하루 여행 vlog");
        assertThat(response.channelName()).isEqualTo("하루");
        assertThat(response.uploadedDate()).isEqualTo(LocalDate.of(2025, 10, 25));
    }

    @Test
    @DisplayName("API가 비디오 정보를 찾지 못했을 때 NotFoundException을 던진다")
    void searchByVideoId_withNonExistentVideoId_throwsNotFoundException() {
        // given
        String videoId = "nonexistent";
        String mockResponse = " { \"items\": [] }";

        String expectedUrl = youtubeApiUrl + "?part=snippet&id=" + videoId + "&key=" + youtubeApiKey;
        mockRestServiceServer.expect(requestTo(expectedUrl))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        // when & then
        assertThatThrownBy(() -> youtubeVideoSearchClient.searchByVideoId(videoId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(ErrorTag.YOUTUBE_VIDEO_NOT_FOUND.getMessage());
        mockRestServiceServer.verify();
    }

    @Test
    @DisplayName("유튜브 API가 4xx 에러를 반환할 때 BadRequestException을 던진다")
    void searchByVideoId_whenApiReturns4xx_throwsBadRequestException() {
        // given
        String videoId = "someVideo";

        String expectedUrl = youtubeApiUrl + "?part=snippet&id=" + videoId + "&key=" + youtubeApiKey;
        mockRestServiceServer.expect(requestTo(expectedUrl))
                .andRespond(withBadRequest());

        // when & then
        assertThatThrownBy(() -> youtubeVideoSearchClient.searchByVideoId(videoId))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorTag.YOUTUBE_API_REQUEST_FAILED.getMessage());
        mockRestServiceServer.verify();
    }
}
