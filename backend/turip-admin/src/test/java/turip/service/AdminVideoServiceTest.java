package turip.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.controller.dto.response.AdminVideoResponse;
import turip.infrastructure.client.YoutubeVideoSearchClient;
import turip.infrastructure.client.dto.YoutubeVideoSearchResponse;

@ExtendWith(MockitoExtension.class)
class AdminVideoServiceTest {

    @InjectMocks
    private AdminVideoService adminVideoService;

    @Mock
    private YoutubeVideoSearchClient youtubeVideoSearchClient;

    @Test
    @DisplayName("유효한 유튜브 URL로 영상 정보를 성공적으로 가져온다")
    void getVideoData_withValidUrl_returnsVideoResponse() {
        // given
        String videoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
        String videoId = "dQw4w9WgXcQ";
        YoutubeVideoSearchResponse clientResponse = YoutubeVideoSearchResponse.of(
                videoId,
                "하루 여행 vlog",
                "하루",
                LocalDate.of(2025, 10, 25)
        );

        when(youtubeVideoSearchClient.searchByVideoId(videoId)).thenReturn(clientResponse);

        // when
        AdminVideoResponse response = adminVideoService.getVideoData(videoUrl);

        // then
        assertThat(response.videoId()).isEqualTo(videoId);
        assertThat(response.videoTitle()).isEqualTo("하루 여행 vlog");
        assertThat(response.channelName()).isEqualTo("하루");
        assertThat(response.uploadDate()).isEqualTo(LocalDate.of(2025, 10, 25));
    }

    @Test
    @DisplayName("유효하지 않은 유튜브 URL 형식일 경우 BadRequestException을 던진다")
    void getVideoData_withInvalidUrl_throwsBadRequestException() {
        // given
        String invalidUrl = "https://invalid.url/test";

        // when & then
        assertThatThrownBy(() -> adminVideoService.getVideoData(invalidUrl))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorTag.INVALID_YOUTUBE_URL.getMessage());
    }
}

