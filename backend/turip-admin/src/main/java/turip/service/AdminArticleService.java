package turip.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.TuripMember;
import turip.article.domain.Article;
import turip.article.domain.ArticlePlace;
import turip.article.domain.ArticleTag;
import turip.article.domain.Tag;
import turip.article.repository.ArticlePlaceRepository;
import turip.article.repository.ArticleRepository;
import turip.article.repository.ArticleTagRepository;
import turip.article.repository.TagRepository;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.NotFoundException;
import turip.controller.dto.request.AdminArticleCreateRequest;
import turip.controller.dto.response.AdminArticleResponse;
import turip.controller.dto.response.AdminArticleSummaryResponse;
import turip.controller.dto.response.AdminArticlesResponse;
import turip.place.controller.dto.response.PlaceResponse;
import turip.place.domain.Place;
import turip.place.repository.PlaceRepository;

@Service
@RequiredArgsConstructor
public class AdminArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleTagRepository articleTagRepository;
    private final ArticlePlaceRepository articlePlaceRepository;
    private final TagRepository tagRepository;
    private final PlaceRepository placeRepository;

    @Value("${turip.article.default-thumbnail-url}")
    private String defaultThumbnailUrl;

    @Transactional
    public Long create(AdminArticleCreateRequest request, TuripMember admin) {
        int displayOrder = articleRepository.findMinDisplayOrder()
                .map(min -> min - 1)
                .orElse(0);

        Article article = new Article(
                request.title(),
                request.subtitle(),
                request.content(),
                request.thumbnailUrl(),
                admin.getMember().getAccount(),
                displayOrder,
                request.isPublished()
        );
        articleRepository.save(article);

        saveArticleTags(article, request.tagNames());
        saveArticlePlaces(article, request.placeIds());

        return article.getId();
    }

    public AdminArticlesResponse findArticles(Integer size, Long lastId) {
        Slice<Article> slice = findArticleSlice(size, lastId);
        List<Article> articles = slice.getContent();

        if (articles.isEmpty()) {
            return AdminArticlesResponse.of(new ArrayList<>(), slice.hasNext());
        }

        Map<Long, List<String>> tagNamesByArticleId = findTagNamesByArticleIds(articles);

        List<AdminArticleSummaryResponse> summaries = articles.stream()
                .map(article -> AdminArticleSummaryResponse.of(
                        article,
                        resolveThumbnailUrl(article),
                        tagNamesByArticleId.getOrDefault(article.getId(), List.of())
                ))
                .toList();

        return AdminArticlesResponse.of(summaries, slice.hasNext());
    }

    public AdminArticleResponse getArticle(Long articleId) {
        Article article = findArticle(articleId);

        List<String> tagNames = articleTagRepository.findAllByArticleId(articleId).stream()
                .map(articleTag -> articleTag.getTag().getName())
                .toList();

        List<PlaceResponse> places = articlePlaceRepository.findAllByArticleId(articleId).stream()
                .map(articlePlace -> PlaceResponse.from(articlePlace.getPlace()))
                .toList();

        return AdminArticleResponse.of(article, resolveThumbnailUrl(article), tagNames, places);
    }

    private void saveArticleTags(Article article, List<String> tagNames) {
        if (tagNames.isEmpty()) {
            return;
        }

        Map<String, Tag> existingTagsByName = tagRepository.findAllByNameIn(tagNames).stream()
                .collect(Collectors.toMap(Tag::getName, tag -> tag));

        for (String tagName : tagNames) {
            Tag tag = existingTagsByName.computeIfAbsent(tagName, name -> tagRepository.save(new Tag(name)));
            articleTagRepository.save(new ArticleTag(article, tag));
        }
    }

    private void saveArticlePlaces(Article article, List<Long> placeIds) {
        List<Place> places = placeRepository.findAllById(placeIds);
        for (Place place : places) {
            articlePlaceRepository.save(new ArticlePlace(article, place));
        }
    }

    private Slice<Article> findArticleSlice(Integer size, Long lastId) {
        PageRequest pageable = PageRequest.of(0, size);
        if (lastId == null) {
            return articleRepository.findFirstPage(false, pageable);
        }
        Article cursorArticle = findArticle(lastId);
        return articleRepository.findNextPage(false, cursorArticle.getDisplayOrder(), pageable);
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

    private Article findArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.ARTICLE_NOT_FOUND));
    }
}
