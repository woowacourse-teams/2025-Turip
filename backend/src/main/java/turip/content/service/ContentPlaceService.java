package turip.content.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.NotFoundException;
import turip.content.controller.dto.response.content.TripDurationResponse;
import turip.content.controller.dto.response.place.ContentPlaceDetailResponse;
import turip.content.controller.dto.response.place.ContentPlaceResponse;
import turip.content.domain.ContentPlace;
import turip.content.repository.ContentPlaceRepository;
import turip.content.repository.ContentRepository;
import turip.favorite.repository.FavoritePlaceRepository;
import turip.member.domain.Member;
import turip.place.domain.Place;

@Service
@RequiredArgsConstructor
public class ContentPlaceService {

    private final ContentPlaceRepository contentPlaceRepository;
    private final ContentRepository contentRepository;
    private final FavoritePlaceRepository favoritePlaceRepository;

    public Map<Long, TripDurationResponse> calculateDurations(List<Long> contentIds) {
        List<ContentPlace> contentPlaces = contentPlaceRepository.findAllByContentIdIn(contentIds);
        Map<Long, Integer> maxVisitDays = contentPlaces.stream()
                .collect(Collectors.groupingBy(
                        cp -> cp.getContent().getId(),
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingInt(ContentPlace::getVisitDay)),
                                opt -> opt.map(ContentPlace::getVisitDay).orElse(0)
                        )
                ));

        return contentIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> {
                            int maxDay = maxVisitDays.getOrDefault(id, 0);
                            return TripDurationResponse.of(maxDay > 0 ? maxDay - 1 : 0, maxDay);
                        }
                ));
    }

    public Map<Long, Integer> countPlacesByContentIds(List<Long> contentIds) {
        List<ContentPlace> contentPlaces = contentPlaceRepository.findAllByContentIdIn(contentIds);
        return contentPlaces.stream()
                .collect(Collectors.groupingBy(
                        cp -> cp.getContent().getId(),
                        Collectors.collectingAndThen(
                                Collectors.mapping(ContentPlace::getPlace, Collectors.toSet()),
                                Set::size
                        )
                ));
    }

    public int countByContentId(Long contentId) {
        return contentPlaceRepository.countByContentId(contentId);
    }

    public ContentPlaceDetailResponse findContentPlaceDetails(Member member, Long contentId) {
        validateContentExists(contentId);
        List<ContentPlace> contentPlaces = contentPlaceRepository.findAllByContentId(contentId);
        int days = calculateDurationDays(contentId);
        int nights = days - 1;
        int contentPlaceCount = calculatePlaceCount(contentPlaces);
        List<ContentPlaceResponse> contentPlaceResponse = parseContentPlaceToContentPlaceResponse(member,
                contentPlaces);

        return ContentPlaceDetailResponse.of(nights, days, contentPlaceCount, contentPlaceResponse);
    }

    public int calculateDurationDays(Long contentId) {
        return contentPlaceRepository.findAllByContentId(contentId)
                .stream()
                .mapToInt(ContentPlace::getVisitDay)
                .max()
                .orElse(0);
    }

    private void validateContentExists(Long contentId) {
        boolean isContentExists = contentRepository.existsById(contentId);
        if (!isContentExists) {
            throw new NotFoundException(ErrorTag.CONTENT_NOT_FOUND);
        }
    }

    private int calculatePlaceCount(List<ContentPlace> contentPlaces) {
        return (int) contentPlaces.stream()
                .map(ContentPlace::getPlace)
                .distinct()
                .count();
    }

    private List<ContentPlaceResponse> parseContentPlaceToContentPlaceResponse(Member member,
                                                                               List<ContentPlace> contentPlaces) {
        List<Place> places = contentPlaces.stream()
                .map(ContentPlace::getPlace)
                .distinct()
                .toList();
        Set<Long> favoritedPlaceIds = favoritePlaceRepository.findFavoritedPlaceIdsByFavoriteFolderMemberAndPlaceIn(
                member, places);
        return contentPlaces.stream()
                .map(contentPlace -> {
                    boolean isFavoritePlace = favoritedPlaceIds.contains(contentPlace.getPlace().getId());
                    return ContentPlaceResponse.of(contentPlace, isFavoritePlace);
                })
                .toList();
    }
}
