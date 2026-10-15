package turip.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class YoutubeVideoUrlParserTest {

    @Test
    @DisplayName("videoId 11자 그대로 주어지면 그대로 반환한다")
    void extractVideoId_withRawVideoId_returnsAsIs() {
        // given
        String input = "dQw4w9WgXcQ";

        // when
        String result = YoutubeVideoUrlParser.extractVideoId(input);

        // then
        assertThat(result).isEqualTo("dQw4w9WgXcQ");
    }

    @Test
    @DisplayName("일반 watch URL에서 videoId를 추출한다")
    void extractVideoId_withWatchUrl_extractsVideoId() {
        // given
        String input = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";

        // when
        String result = YoutubeVideoUrlParser.extractVideoId(input);

        // then
        assertThat(result).isEqualTo("dQw4w9WgXcQ");
    }

    @Test
    @DisplayName("youtu.be 단축 URL에서 videoId를 추출한다")
    void extractVideoId_withShortUrl_extractsVideoId() {
        // given
        String input = "https://youtu.be/dQw4w9WgXcQ";

        // when
        String result = YoutubeVideoUrlParser.extractVideoId(input);

        // then
        assertThat(result).isEqualTo("dQw4w9WgXcQ");
    }

    @Test
    @DisplayName("유효하지 않은 URL이면 null을 반환한다")
    void extractVideoId_withInvalidUrl_returnsNull() {
        // given
        String input = "https://invalid.url/test";

        // when
        String result = YoutubeVideoUrlParser.extractVideoId(input);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("입력이 null이거나 공백이면 null을 반환한다")
    void extractVideoId_withNullOrBlank_returnsNull() {
        assertThat(YoutubeVideoUrlParser.extractVideoId(null)).isNull();
        assertThat(YoutubeVideoUrlParser.extractVideoId("  ")).isNull();
    }
}
