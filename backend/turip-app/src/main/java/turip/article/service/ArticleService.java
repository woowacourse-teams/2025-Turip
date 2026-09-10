package turip.article.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import turip.article.controller.dto.response.ArticleResponse;
import turip.article.controller.dto.response.ArticleSummaryResponse;
import turip.article.controller.dto.response.ArticlesResponse;
import turip.article.domain.Article;
import turip.article.repository.ArticlePlaceRepository;
import turip.article.repository.ArticleRepository;
import turip.article.repository.ArticleTagRepository;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.NotFoundException;
import turip.place.controller.dto.response.PlaceResponse;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleTagRepository articleTagRepository;
    private final ArticlePlaceRepository articlePlaceRepository;

    @Value("${turip.article.default-thumbnail-url}")
    private String defaultThumbnailUrl;

    public ArticlesResponse findArticles(Integer size, Long lastId) {
        Slice<Article> slice = findArticleSlice(size, lastId);
        List<Article> articles = slice.getContent();

        if (articles.isEmpty()) {
            return ArticlesResponse.of(new ArrayList<>(), slice.hasNext());
        }

        Map<Long, List<String>> tagNamesByArticleId = findTagNamesByArticleIds(articles);

        List<ArticleSummaryResponse> summaries = articles.stream()
                .map(article -> ArticleSummaryResponse.of(
                        article,
                        resolveThumbnailUrl(article),
                        tagNamesByArticleId.getOrDefault(article.getId(), List.of())
                ))
                .toList();

        return ArticlesResponse.of(summaries, slice.hasNext());
    }

    public ArticleResponse getArticle(Long articleId) {
        Article article = findPublishedArticle(articleId);

        List<String> tagNames = articleTagRepository.findAllByArticleId(articleId).stream()
                .map(articleTag -> articleTag.getTag().getName())
                .toList();

        List<PlaceResponse> places = articlePlaceRepository.findAllByArticleId(articleId).stream()
                .map(articlePlace -> PlaceResponse.from(articlePlace.getPlace()))
                .toList();

        return ArticleResponse.of(article, resolveThumbnailUrl(article), tagNames, places);
    }

    private Slice<Article> findArticleSlice(Integer size, Long lastId) {
        PageRequest pageable = PageRequest.of(0, size);
        if (lastId == null) {
            return articleRepository.findFirstPageByIsPublishedTrue(pageable);
        }
        Article cursorArticle = findPublishedArticle(lastId);
        return articleRepository.findNextPageByIsPublishedTrue(cursorArticle.getDisplayOrder(), pageable);
    }

    private Article findPublishedArticle(Long articleId) {
        return articleRepository.findByIdAndIsPublishedTrue(articleId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.ARTICLE_NOT_FOUND));
    }

    private Map<Long, List<String>> findTagNamesByArticleIds(List<Article> articles) {
        List<Long> articleIds = articles.stream()
                .map(Article::getId)
                .toList();

        return articleTagRepository.findAllByArticleIdIn(articleIds).stream()
                .collect(Collectors.groupingBy(
                        articleTag -> articleTag.getArticle().getId(),
                        Collectors.mapping(articleTag -> articleTag.getTag().getName(), Collectors.toList())
                ));
    }

    private String resolveThumbnailUrl(Article article) {
        if (article.getThumbnailUrl() == null) {
            return defaultThumbnailUrl;
        }
        return article.getThumbnailUrl();
    }
}
