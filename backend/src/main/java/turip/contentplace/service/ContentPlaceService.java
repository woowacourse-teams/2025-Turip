package turip.contentplace.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.content.repository.ContentRepository;
import turip.contentplace.controller.dto.response.ContentPlaceDetailResponse;
import turip.contentplace.controller.dto.response.ContentPlaceResponse;
import turip.contentplace.domain.ContentPlace;
import turip.contentplace.repository.ContentPlaceRepository;
import turip.exception.custom.NotFoundException;
import turip.favoriteplace.repository.FavoritePlaceRepository;
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

        List<ContentPlaceResponse> contentPlaceResponse = contentPlaces.stream()
                .map(contentPlace -> {
                    boolean isFavoritePlace = favoritePlaceRepository.existsByFavoriteFolderMemberAndPlace(member,
                            contentPlace.getPlace());
                    return ContentPlaceResponse.of(contentPlace, isFavoritePlace);
                })
                .toList();

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
            throw new NotFoundException("해당 id에 대한 컨텐츠가 존재하지 않습니다.");
        }
    }

    private int calculatePlaceCount(List<ContentPlace> contentPlaces) {
        return (int) contentPlaces.stream()
                .map(ContentPlace::getPlace)
                .distinct()
                .count();
    }
}
