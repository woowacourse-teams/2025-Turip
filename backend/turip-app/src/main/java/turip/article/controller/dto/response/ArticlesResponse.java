package turip.article.controller.dto.response;

import java.util.List;

public record ArticlesResponse(
        List<ArticleSummaryResponse> articles,
        boolean loadable
) {

    public static ArticlesResponse of(List<ArticleSummaryResponse> articles, boolean loadable) {
        return new ArticlesResponse(articles, loadable);
    }
}
