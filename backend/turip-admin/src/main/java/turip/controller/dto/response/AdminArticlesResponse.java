package turip.controller.dto.response;

import java.util.List;

public record AdminArticlesResponse(
        List<AdminArticleSummaryResponse> articles,
        boolean loadable
) {

    public static AdminArticlesResponse of(List<AdminArticleSummaryResponse> articles, boolean loadable) {
        return new AdminArticlesResponse(articles, loadable);
    }
}
