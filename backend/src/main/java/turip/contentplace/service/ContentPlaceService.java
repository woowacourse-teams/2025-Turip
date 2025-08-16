package turip.contentplace.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.contentplace.controller.dto.response.ContentPlaceDetailResponse;
import turip.contentplace.domain.ContentPlace;
import turip.contentplace.repository.ContentPlaceRepository;

@Service
@RequiredArgsConstructor
public class ContentPlaceService {

    private final ContentPlaceRepository contentPlaceRepository;

    public int countByContentId(Long contentId) {
        return contentPlaceRepository.countByContentId(contentId);
    }

    public ContentPlaceDetailResponse findContentPlaceDetails(Long contentId) {
        List<ContentPlace> contentPlaces = contentPlaceRepository.findAllByContentId(contentId);
        int days = calculateDurationDays(contentId);
        int nights = days - 1;
        int contentPlaceCount = calculatePlaceCount(contentPlaces);
        return ContentPlaceDetailResponse.of(nights, days, contentPlaceCount, contentPlaces);
    }

    public int calculateDurationDays(Long contentId) {
        return contentPlaceRepository.findAllByContentId(contentId)
                .stream()
                .mapToInt(ContentPlace::getVisitDay)
                .max()
                .orElse(0);
    }

    private int calculatePlaceCount(List<ContentPlace> contentPlaces) {
        return (int) contentPlaces.stream()
                .map(ContentPlace::getPlace)
                .distinct()
                .count();
    }
}
