package turip.controller.dto.request;

import java.util.List;

public record AdminArticleUpdateRequest(
        String title,
        String subtitle,
        String content,
        String thumbnailUrl,
        boolean isPublished,
        List<String> tagNames,
        List<Long> placeIds
) {
}
