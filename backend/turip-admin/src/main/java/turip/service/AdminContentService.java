package turip.service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.common.exception.ErrorTag;
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
import turip.place.domain.Place;
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

    @Transactional
    public Long save(AdminContentSaveRequest request) {
        Creator creator = findOrCreateCreator(request);
        City city = findCity(request);
        Content content = createContent(request, creator, city);
        Content savedContent = contentRepository.save(content);

        request.contentPlaces().forEach(contentPlaceRequest -> {
            AdminContentSaveRequest.PlaceRequest placeRequest = contentPlaceRequest.place();
            Place place = findOrCreatePlace(placeRequest);
            ContentPlace newContentPlace = createContentPlace(contentPlaceRequest, place, content);
            contentPlaceRepository.save(newContentPlace);
        });

        return savedContent.getId();
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

    private Content createContent(AdminContentSaveRequest request, Creator creator, City city) {
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
        return LocalTime.parse("00:" + timeLine, DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
