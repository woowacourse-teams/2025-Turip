package turip.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
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
import turip.common.exception.custom.BadRequestException;
import turip.common.exception.custom.NotFoundException;
import turip.controller.dto.request.AdminArticleCreateRequest;
import turip.controller.dto.request.AdminArticleOrderItem;
import turip.controller.dto.request.AdminArticleOrderRequest;
import turip.controller.dto.request.AdminArticleUpdateRequest;
import turip.controller.dto.response.AdminArticleResponse;
import turip.controller.dto.response.AdminArticleSummaryResponse;
import turip.controller.dto.response.AdminArticlesResponse;
import turip.infrastructure.AdminArticleImageUploader;
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
    private final AdminArticleImageUploader adminArticleImageUploader;

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

        updateArticleTags(article, request.tagNames());
        updateArticlePlaces(article, request.placeIds());

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
        Article article = getById(articleId);

        List<String> tagNames = articleTagRepository.findAllByArticleId(articleId).stream()
                .map(articleTag -> articleTag.getTag().getName())
                .toList();

        List<PlaceResponse> places = articlePlaceRepository.findAllByArticleId(articleId).stream()
                .map(articlePlace -> PlaceResponse.from(articlePlace.getPlace()))
                .toList();

        return AdminArticleResponse.of(article, resolveThumbnailUrl(article), tagNames, places);
    }

    @Transactional
    public AdminArticleResponse update(Long articleId, AdminArticleUpdateRequest request) {
        Article article = getById(articleId);
        article.update(request.title(), request.subtitle(), request.content(), request.thumbnailUrl(),
                request.isPublished());

        updateArticleTags(article, request.tagNames());
        updateArticlePlaces(article, request.placeIds());

        List<String> tagNames = articleTagRepository.findAllByArticleId(articleId).stream()
                .map(articleTag -> articleTag.getTag().getName())
                .toList();

        List<PlaceResponse> places = articlePlaceRepository.findAllByArticleId(articleId).stream()
                .map(articlePlace -> PlaceResponse.from(articlePlace.getPlace()))
                .toList();

        return AdminArticleResponse.of(article, resolveThumbnailUrl(article), tagNames, places);
    }

    @Transactional
    public void remove(Long articleId) {
        Article article = getById(articleId);
        articleRepository.delete(article);
    }

    public String uploadImage(MultipartFile image) {
        return adminArticleImageUploader.upload(image);
    }

    @Transactional
    public void reorder(AdminArticleOrderRequest request) {
        List<Long> requestIds = request.orders().stream()
                .map(AdminArticleOrderItem::id)
                .toList();

        List<Article> articles = articleRepository.findAllById(requestIds);
        validateReorderRequest(request, articles);

        Map<Long, Integer> newDisplayOrderById = request.orders().stream()
                .collect(Collectors.toMap(AdminArticleOrderItem::id, AdminArticleOrderItem::displayOrder));

        for (Article article : articles) {
            article.reorder(newDisplayOrderById.get(article.getId()));
        }
    }

    private void updateArticleTags(Article article, List<String> tagNames) {
        Set<String> requestedTagNames = new HashSet<>(tagNames);

        List<ArticleTag> existingArticleTags = articleTagRepository.findAllByArticleId(article.getId());
        Map<String, ArticleTag> existingArticleTagsByTagName = existingArticleTags.stream()
                .collect(Collectors.toMap(articleTag -> articleTag.getTag().getName(), articleTag -> articleTag));

        List<ArticleTag> articleTagsToDelete = existingArticleTags.stream()
                .filter(articleTag -> !requestedTagNames.contains(articleTag.getTag().getName()))
                .toList();
        articleTagRepository.deleteAll(articleTagsToDelete);

        Set<String> tagNamesToAdd = new HashSet<>(requestedTagNames);
        tagNamesToAdd.removeAll(existingArticleTagsByTagName.keySet());
        if (tagNamesToAdd.isEmpty()) {
            return;
        }

        Map<String, Tag> existingTagsByName = tagRepository.findAllByNameIn(new ArrayList<>(tagNamesToAdd)).stream()
                .collect(Collectors.toMap(Tag::getName, tag -> tag));

        for (String tagName : tagNamesToAdd) {
            Tag tag = existingTagsByName.computeIfAbsent(tagName, name -> tagRepository.save(new Tag(name)));
            articleTagRepository.save(new ArticleTag(article, tag));
        }
    }

    private void updateArticlePlaces(Article article, List<Long> placeIds) {
        Set<Long> requestedPlaceIds = new HashSet<>(placeIds);

        List<ArticlePlace> existingArticlePlaces = articlePlaceRepository.findAllByArticleId(article.getId());
        Set<Long> existingPlaceIds = existingArticlePlaces.stream()
                .map(articlePlace -> articlePlace.getPlace().getId())
                .collect(Collectors.toSet());

        List<ArticlePlace> articlePlacesToDelete = existingArticlePlaces.stream()
                .filter(articlePlace -> !requestedPlaceIds.contains(articlePlace.getPlace().getId()))
                .toList();
        articlePlaceRepository.deleteAll(articlePlacesToDelete);

        Set<Long> placeIdsToAdd = new HashSet<>(requestedPlaceIds);
        placeIdsToAdd.removeAll(existingPlaceIds);
        if (placeIdsToAdd.isEmpty()) {
            return;
        }

        List<Place> placesToAdd = placeRepository.findAllById(new ArrayList<>(placeIdsToAdd));
        for (Place place : placesToAdd) {
            articlePlaceRepository.save(new ArticlePlace(article, place));
        }
    }

    private Slice<Article> findArticleSlice(Integer size, Long lastId) {
        PageRequest pageable = PageRequest.of(0, size);
        if (lastId == null) {
            return articleRepository.findFirstPage(false, pageable);
        }
        Article cursorArticle = getById(lastId);
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

    private Article getById(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.ARTICLE_NOT_FOUND));
    }
    
    private void validateReorderRequest(AdminArticleOrderRequest request, List<Article> articles) {
        if (articles.size() != request.orders().size()) {
            throw new BadRequestException(ErrorTag.ARTICLE_ORDER_INVALID);
        }

        Set<Integer> currentDisplayOrders = articles.stream()
                .map(Article::getDisplayOrder)
                .collect(Collectors.toSet());
        Set<Integer> requestedDisplayOrders = request.orders().stream()
                .map(AdminArticleOrderItem::displayOrder)
                .collect(Collectors.toSet());

        if (!currentDisplayOrders.equals(requestedDisplayOrders)) {
            throw new BadRequestException(ErrorTag.ARTICLE_ORDER_INVALID);
        }
    }
}
