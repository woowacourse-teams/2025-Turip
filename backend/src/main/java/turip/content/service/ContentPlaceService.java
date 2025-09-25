package turip.content.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.NotFoundException;
import turip.content.controller.dto.response.place.ContentPlaceDetailResponse;
import turip.content.controller.dto.response.place.ContentPlaceResponse;
import turip.content.domain.ContentPlace;
import turip.content.repository.ContentPlaceRepository;
import turip.content.repository.ContentRepository;
import turip.favorite.repository.FavoritePlaceRepository;
import turip.member.domain.Member;

@Service
@RequiredArgsConstructor
public class ContentPlaceService {

    private final ContentPlaceRepository contentPlaceRepository;
    private final ContentRepository contentRepository;
    private final FavoritePlaceRepository favoritePlaceRepository;

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
        return contentPlaces.stream()
                .map(contentPlace -> {
                    boolean isFavoritePlace = favoritePlaceRepository.existsByFavoriteFolderMemberAndPlace(member,
                            contentPlace.getPlace());
                    return ContentPlaceResponse.of(contentPlace, isFavoritePlace);
                })
                .toList();
    }
}
