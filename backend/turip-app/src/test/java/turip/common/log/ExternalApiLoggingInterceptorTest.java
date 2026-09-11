package turip.common.log;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalApiLoggingInterceptorTest {

    private final ExternalApiLoggingInterceptor interceptor = new ExternalApiLoggingInterceptor();

    @Test
    @DisplayName("URI의 쿼리 파라미터에 민감한 정보(key)가 포함되어 있으면 마스킹한다.")
    void maskSensitiveParamsWithKey() throws Exception {
        // given
        URI uri = new URI("https://www.googleapis.com/youtube/v3/videos?part=snippet&id=videoId&key=AIzaSyA_...&other=param");
        Method method = ExternalApiLoggingInterceptor.class.getDeclaredMethod("maskSensitiveParams", URI.class);
        method.setAccessible(true);

        // when
        String maskedUri = (String) method.invoke(interceptor, uri);

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
        Method method = ExternalApiLoggingInterceptor.class.getDeclaredMethod("maskSensitiveParams", URI.class);
        method.setAccessible(true);

        // when
        String maskedUri = (String) method.invoke(interceptor, uri);

        // then
        assertThat(maskedUri).contains("apiKey=***");
        assertThat(maskedUri).doesNotContain("AIzaSyA_...");
        assertThat(maskedUri).contains("other=param");
    }

    @Test
    @DisplayName("URI의 쿼리 파라미터에 민감한 정보(serviceKey)가 percent-encoding을 포함해도 마스킹한다.")
    void maskSensitiveParamsWithServiceKey() throws Exception {
        // given
        URI uri = new URI(
                "https://apis.data.go.kr/B551011/PhokoAwrdService/phokoAwrdList?serviceKey=dummyServiceKey%2FAbCdEfGhIjKlMnOp%2BQrStUvWxYz%3D%3D&MobileOS=AND&MobileApp=Turip&lDongRegnCd=26&_type=json&numOfRows=1");
        Method method = ExternalApiLoggingInterceptor.class.getDeclaredMethod("maskSensitiveParams", URI.class);
        method.setAccessible(true);

        // when
        String maskedUri = (String) method.invoke(interceptor, uri);

        // then
        assertThat(maskedUri).contains("serviceKey=***");
        assertThat(maskedUri).doesNotContain("dummyServiceKey");
        assertThat(maskedUri).contains("lDongRegnCd=26");
    }

    @Test
    @DisplayName("URI의 쿼리 파라미터에 민감한 정보가 없으면 그대로 반환한다.")
    void maskSensitiveParamsWithoutSensitiveInfo() throws Exception {
        // given
        URI uri = new URI("https://www.googleapis.com/youtube/v3/videos?part=snippet&id=videoId&other=param");
        Method method = ExternalApiLoggingInterceptor.class.getDeclaredMethod("maskSensitiveParams", URI.class);
        method.setAccessible(true);

        // when
        String maskedUri = (String) method.invoke(interceptor, uri);

        // then
        assertThat(maskedUri).isEqualTo(uri.toString());
    }

    @Test
    @DisplayName("URI에 쿼리 파라미터가 없으면 그대로 반환한다.")
    void maskSensitiveParamsWithoutQuery() throws Exception {
        // given
        URI uri = new URI("https://www.googleapis.com/youtube/v3/videos");
        Method method = ExternalApiLoggingInterceptor.class.getDeclaredMethod("maskSensitiveParams", URI.class);
        method.setAccessible(true);

        // when
        String maskedUri = (String) method.invoke(interceptor, uri);

        // then
        assertThat(maskedUri).isEqualTo(uri.toString());
    }
}
