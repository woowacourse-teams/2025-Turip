package turip.common.log;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExternalApiLogFormatTest {

    @Test
    @DisplayName("URI의 쿼리 파라미터에 민감한 정보(key)가 포함되어 있으면 마스킹한다.")
    void maskSensitiveParamsWithKey() throws Exception {
        // given
        URI uri = new URI("https://www.googleapis.com/youtube/v3/videos?part=snippet&id=videoId&key=AIzaSyA_...&other=param");

        // when
        String maskedUri = ExternalApiLogFormat.maskSensitiveParams(uri);

        // then
        assertThat(maskedUri).contains("key=***");
        assertThat(maskedUri).doesNotContain("AIzaSyA_...");
        assertThat(maskedUri).contains("other=param");
    }

    @Test
    @DisplayName("URI의 쿼리 파라미터에 민감한 정보(apiKey)가 포함되어 있으면 마스킹한다.")
    void maskSensitiveParamsWithApiKey() throws Exception {
        // given
        URI uri = new URI("https://www.googleapis.com/youtube/v3/videos?part=snippet&id=videoId&apiKey=AIzaSyA_...&other=param");

        // when
        String maskedUri = ExternalApiLogFormat.maskSensitiveParams(uri);

        // then
        assertThat(maskedUri).contains("apiKey=***");
        assertThat(maskedUri).doesNotContain("AIzaSyA_...");
        assertThat(maskedUri).contains("other=param");
    }

    @Test
    @DisplayName("URI의 쿼리 파라미터에 민감한 정보가 없으면 그대로 반환한다.")
    void maskSensitiveParamsWithoutSensitiveInfo() throws Exception {
        // given
        URI uri = new URI("https://www.googleapis.com/youtube/v3/videos?part=snippet&id=videoId&other=param");

        // when
        String maskedUri = ExternalApiLogFormat.maskSensitiveParams(uri);

        // then
        assertThat(maskedUri).isEqualTo(uri.toString());
    }

    @Test
    @DisplayName("URI에 쿼리 파라미터가 없으면 그대로 반환한다.")
    void maskSensitiveParamsWithoutQuery() throws Exception {
        // given
        URI uri = new URI("https://www.googleapis.com/youtube/v3/videos");

        // when
        String maskedUri = ExternalApiLogFormat.maskSensitiveParams(uri);

        // then
        assertThat(maskedUri).isEqualTo(uri.toString());
    }
}
