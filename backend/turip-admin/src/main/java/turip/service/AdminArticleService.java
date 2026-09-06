package turip.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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
import turip.controller.dto.request.AdminArticleCreateRequest;
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
}
