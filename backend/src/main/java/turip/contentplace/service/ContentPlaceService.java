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
        return contentPlaceRepository.countByContentId(contentId);
    }

    public ContentPlaceDetailResponse findContentPlaceDetails(Long contentId) {
        validateContentExists(contentId);
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
