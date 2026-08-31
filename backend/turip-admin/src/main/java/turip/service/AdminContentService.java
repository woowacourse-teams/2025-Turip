package turip.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.common.exception.custom.ConflictException;
import turip.common.exception.custom.NotFoundException;
import turip.content.domain.Content;
import turip.content.domain.ContentPlace;
import turip.content.repository.ContentPlaceRepository;
import turip.content.repository.ContentRepository;
import turip.controller.dto.request.AdminContentSaveRequest;
import turip.controller.dto.request.AdminContentSaveRequest.ContentPlaceRequest;
import turip.controller.dto.request.AdminContentSaveRequest.PlaceRequest;
import turip.controller.dto.response.AdminContentResponse;
import turip.controller.dto.response.AdminContentsResponse;
import turip.creator.domain.Creator;
import turip.creator.repository.CreatorRepository;
import turip.favorite.repository.FavoriteContentRepository;
import turip.place.domain.Category;
import turip.place.domain.Place;
import turip.place.domain.PlaceCategory;
import turip.place.repository.CategoryRepository;
import turip.place.repository.PlaceCategoryRepository;
import turip.place.repository.PlaceRepository;
import turip.region.domain.City;
import turip.region.repository.CityRepository;

@Service
@RequiredArgsConstructor
public class AdminContentService {

    private static final int DAYS_UNTIL_SUNDAY = 6;
    private static final int ONE_WEEK = 1;

    private final ContentRepository contentRepository;
    private final PlaceRepository placeRepository;
    private final ContentPlaceRepository contentPlaceRepository;
    private final CreatorRepository creatorRepository;
    private final CityRepository cityRepository;
    private final CategoryRepository categoryRepository;
    private final PlaceCategoryRepository placeCategoryRepository;
    private final FavoriteContentRepository favoriteContentRepository;

    @Transactional
    public Long save(AdminContentSaveRequest request) {
        Creator creator = findOrCreateCreator(request);
        City city = findCity(request);
        Content content = findOrCreateContent(request, creator, city);
        contentRepository.save(content);

        request.contentPlaces().forEach(contentPlaceRequest -> {
            AdminContentSaveRequest.PlaceRequest placeRequest = contentPlaceRequest.place();
            Place place = findOrCreatePlace(placeRequest);

            if (placeRequest.categoryName() != null && !placeRequest.categoryName().isBlank()) {
                Category category = findOrCreateCategory(placeRequest.categoryName());
                findOrCreatePlaceCategory(place, category);
            }

            ContentPlace newContentPlace = createContentPlace(contentPlaceRequest, place, content);
            contentPlaceRepository.save(newContentPlace);
        });

        return content.getId();
    }

    public AdminContentsResponse findContents(String keyword, long lastId, int size) {
        long targetLastId = lastId;
        if (lastId == 0) {
            targetLastId = Long.MAX_VALUE;
        }
        Slice<Content> contentSlice = findContentSlice(keyword, targetLastId, size);

        List<AdminContentResponse> contents = contentSlice.getContent().stream()
                .map(AdminContentResponse::from)
                .toList();
        return AdminContentsResponse.of(contents, contentSlice.hasNext());
    }

    public AdminContentsResponse findWeeklyPopularContents(int size) {
        List<LocalDate> lastWeekPeriod = getLastWeekPeriod();
        LocalDate startDate = lastWeekPeriod.getFirst();
        LocalDate endDate = lastWeekPeriod.getLast();

        List<Content> popularContents = favoriteContentRepository.findPopularContentsByFavoriteBetweenDatesWithLimit(
                startDate, endDate, size);

        List<AdminContentResponse> contents = popularContents.stream()
                .map(AdminContentResponse::from)
                .toList();
        return AdminContentsResponse.of(contents, false);
    }

    private City findCity(AdminContentSaveRequest request) {
        return cityRepository.findByName(request.cityName())
                .orElseThrow(() -> new NotFoundException(ErrorTag.CITY_NOT_FOUND));
    }

    private ContentPlace createContentPlace(ContentPlaceRequest contentPlaceRequest, Place place,
                                            Content content) {
        return new ContentPlace(
                contentPlaceRequest.visitDay(),
                contentPlaceRequest.visitOrder(),
                parseTimeLine(contentPlaceRequest.timeLine()),
                place,
                content
        );
    }

    private Content findOrCreateContent(AdminContentSaveRequest request, Creator creator, City city) {
        contentRepository.findByCreatorAndTitle(creator, request.video().title())
                .ifPresent(content -> {
                    throw new ConflictException(ErrorTag.CONTENT_SAVE_CONFLICT);
                });

        return new Content(
                creator,
                city,
                request.video().title(),
                request.video().url(),
                request.video().uploadedDate()
        );
    }

    private Place findOrCreatePlace(PlaceRequest placeRequest) {
        return placeRepository.findByUrl(placeRequest.url())
                .orElseGet(() -> placeRepository.save(new Place(
                        placeRequest.name(),
                        placeRequest.url(),
                        placeRequest.address(),
                        placeRequest.latitude(),
                        placeRequest.longitude()
                )));
    }

    private Creator findOrCreateCreator(AdminContentSaveRequest request) {
        return creatorRepository.findByChannelName(request.video().channelName())
                .orElseGet(() -> {
                    Creator creator = new Creator(
                            request.video().channelName(),
                            request.video().channelImage()
                    );

                    return creatorRepository.save(creator);
                });
    }

    private LocalTime parseTimeLine(String timeLine) {
        if (timeLine == null) {
            throw new BadRequestException(ErrorTag.INVALID_TIMELINE_FORMAT);
        }
        try {
            return LocalTime.parse("00:" + timeLine, DateTimeFormatter.ofPattern("HH:mm:ss"));
        } catch (DateTimeParseException e) {
            throw new BadRequestException(ErrorTag.INVALID_TIMELINE_FORMAT);
        }
    }

    private Category findOrCreateCategory(String categoryName) {
        return categoryRepository.findByName(categoryName)
                .orElseGet(() -> categoryRepository.save(new Category(categoryName)));
    }

    private void findOrCreatePlaceCategory(Place place, Category category) {
        placeCategoryRepository.findByPlaceAndCategory(place, category)
                .ifPresentOrElse(
                        placeCategory -> {
                        },
                        () -> placeCategoryRepository.save(new PlaceCategory(place, category))
                );
    }

    private Slice<Content> findContentSlice(String keyword, long lastId, int size) {
        PageRequest pageable = PageRequest.of(0, size);

        // 검색어가 존재하지 않는 경우 전체 콘텐츠 search
        if (keyword == null || keyword.isBlank()) {
            return contentRepository.findAllByIdLessThanOrderByIdDesc(lastId, pageable);
        }

        // 검색어가 존재하는 경우 boolean mode 기반 keyword search
        String booleanModeKeyword = contentRepository.createBooleanModeKeyword(keyword);
        return contentRepository.findByKeywordContaining(booleanModeKeyword, lastId, pageable);
    }

    private List<LocalDate> getLastWeekPeriod() {
        LocalDate thisWeekMonday = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate lastWeekMonday = thisWeekMonday.minusWeeks(ONE_WEEK);
        LocalDate lastWeekSunday = lastWeekMonday.plusDays(DAYS_UNTIL_SUNDAY);
        return List.of(lastWeekMonday, lastWeekSunday);
    }
}
