package turip.common.log;

import java.net.URI;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 외부 API 요청/응답 로깅에서 공통으로 쓰이는 포맷팅 유틸.
 * RestClient(ExternalApiLoggingInterceptor)와 WebClient(ExternalApiLoggingFilter)가
 * 각자의 프레임워크 계약(동기/리액티브)에 맞게 로그를 남기되, 민감정보 마스킹 로직은 여기서 공유한다.
 */
public final class ExternalApiLogFormat {

    private static final Set<String> SENSITIVE_PARAMS = Set.of("key", "apikey", "api_key", "token");

    private ExternalApiLogFormat() {
    }

    public static String maskSensitiveParams(URI uri) {
        String query = uri.getQuery();
        if (query == null) {
            return uri.toString();
        }
        String maskedQuery = Arrays.stream(query.split("&"))
                .map(param -> {
                    String[] kv = param.split("=", 2);
                    if (kv.length == 2 && SENSITIVE_PARAMS.contains(kv[0].toLowerCase())) {
                        return kv[0] + "=***";
                    }
                    return param;
                })
                .collect(Collectors.joining("&"));
        return uri.toString().replace(query, maskedQuery);
    }
}
