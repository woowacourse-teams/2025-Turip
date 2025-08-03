package turip.content.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import turip.content.controller.dto.response.ContentCountResponse;
import turip.content.controller.dto.response.ContentDetailsByCityResponse;
import turip.content.controller.dto.response.ContentResponse;
import turip.content.controller.dto.response.ContentWithoutCityResponse;
import turip.content.controller.dto.response.ContentsByCityResponse;
import turip.content.controller.dto.response.TripDurationResponse;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.exception.NotFoundException;
import turip.tripcourse.service.TripCourseService;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;
    private final TripCourseService tripCourseService;

    public ContentCountResponse countByCityName(String cityName) {
        int count = contentRepository.countByCityName(cityName);
        return ContentCountResponse.from(count);
    }

    public ContentsByCityResponse findContentsByCityName(
            String cityName,
            int size,
            long lastId
    ) {
        Slice<Content> contentSlice = findContentsByCity(cityName, lastId, size);

        List<Content> contents = contentSlice.getContent();
        List<ContentDetailsByCityResponse> contentDetails
                = convertContentsToContentDetailsByRegionResponses(contents);
        boolean loadable = contentSlice.hasNext();

        return ContentsByCityResponse.of(contentDetails, loadable);
    }

    private Slice<Content> findContentsByCity(
            String cityName,
            long lastId,
            int size
    ) {
        Pageable pageable = PageRequest.of(0, size);
        boolean isFirstPage = lastId == 0;

        if (isFirstPage) {
            return contentRepository.findByCityNameOrderByIdDesc(cityName, pageable);
        }
        return contentRepository.findByCityNameAndIdLessThanOrderByIdDesc(cityName, lastId, pageable);
    }

    private List<ContentDetailsByCityResponse> convertContentsToContentDetailsByRegionResponses(
            List<Content> contents) {
        return contents.stream()
                .map(this::toContentDetailsResponse)
                .toList();
    }

    private ContentDetailsByCityResponse toContentDetailsResponse(Content content) {
        ContentWithoutCityResponse contentWithoutRegion = ContentWithoutCityResponse.from(content);
        TripDurationResponse tripDuration = calculateTripDuration(content);
        int tripPlaceCount = tripCourseService.countByContentId(content.getId());

        return ContentDetailsByCityResponse.of(contentWithoutRegion, tripDuration, tripPlaceCount);
    }

    private TripDurationResponse calculateTripDuration(Content content) {
        int totalTripDay = tripCourseService.calculateDurationDays(content.getId());
        return TripDurationResponse.of(totalTripDay - 1, totalTripDay);
    }

    public ContentResponse getById(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("컨텐츠를 찾을 수 없습니다."));
        return ContentResponse.from(content);
    }
}
