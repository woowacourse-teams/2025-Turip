package turip.contentplace.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.content.repository.ContentRepository;
import turip.contentplace.controller.dto.response.ContentPlaceDetailResponse;
import turip.contentplace.domain.ContentPlace;
import turip.contentplace.repository.ContentPlaceRepository;
import turip.exception.custom.NotFoundException;

@Service
@RequiredArgsConstructor
public class ContentPlaceService {

    private final ContentPlaceRepository contentPlaceRepository;
    private final ContentRepository contentRepository;

    public int countByContentId(Long contentId) {
        boolean isContentExists = contentRepository.existsById(contentId);
        if (!isContentExists) {
            throw new NotFoundException("컨텐츠를 찾을 수 없습니다.");
        }
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
