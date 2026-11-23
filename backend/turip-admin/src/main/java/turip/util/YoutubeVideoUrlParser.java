package turip.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeVideoUrlParser {

    private static final String[] VIDEO_ID_PATTERNS = {
            "youtu\\.be/([A-Za-z0-9_-]{11})",
            "v=([A-Za-z0-9_-]{11})",
            "embed/([A-Za-z0-9_-]{11})",
            "shorts/([A-Za-z0-9_-]{11})"
    };

    private static final String WATCH_URL_PREFIX = "https://www.youtube.com/watch?v=";

    private YoutubeVideoUrlParser() {
    }

    /**
     * videoId로 재생 시작 시점(t), 재생목록(list), 공유 추적(si) 등 부가 파라미터가 없는 정규 watch URL을 만든다.
     */
    public static String toWatchUrl(String videoId) {
        return WATCH_URL_PREFIX + videoId;
    }

    public static String extractVideoId(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }
        try {
            String decodedInput = URLDecoder.decode(input, StandardCharsets.UTF_8).trim();
            if (decodedInput.matches("^[A-Za-z0-9_-]{11}$")) {
                return decodedInput;
            }

            for (String pattern : VIDEO_ID_PATTERNS) {
                Matcher matcher = Pattern.compile(pattern).matcher(decodedInput);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
