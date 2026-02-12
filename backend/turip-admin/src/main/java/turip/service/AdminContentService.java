package turip.service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.RequiredArgsConstructor;
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
import turip.creator.domain.Creator;
import turip.creator.repository.CreatorRepository;
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

    private final ContentRepository contentRepository;
    private final PlaceRepository placeRepository;
    private final ContentPlaceRepository contentPlaceRepository;
    private final CreatorRepository creatorRepository;
    private final CityRepository cityRepository;
    private final CategoryRepository categoryRepository;
    private final PlaceCategoryRepository placeCategoryRepository;

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
}

