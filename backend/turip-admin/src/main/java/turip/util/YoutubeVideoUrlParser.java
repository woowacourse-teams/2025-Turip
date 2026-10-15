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

    private YoutubeVideoUrlParser() {
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
