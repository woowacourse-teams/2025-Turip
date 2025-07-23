package turip.content.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import turip.content.controller.dto.response.ContentCountResponse;
import turip.content.controller.dto.response.ContentDetailsByRegionResponse;
import turip.content.controller.dto.response.ContentResponse;
import turip.content.controller.dto.response.ContentWithoutRegionResponse;
import turip.content.controller.dto.response.ContentsByRegionResponse;
import turip.content.controller.dto.response.TripDurationResponse;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.exception.NotFoundException;
import turip.tripcourse.service.TripCourseService;

@Service
@RequiredArgsConstructor
public class ContentService {

    private static final int EXTRA_FETCH_COUNT = 1;

    private final ContentRepository contentRepository;
    private final TripCourseService tripCourseService;

    public ContentCountResponse countByRegionName(String regionName) {
        int count = contentRepository.countByRegion_Name(regionName);
        return ContentCountResponse.from(count);
    }

    public ContentsByRegionResponse findContentsByRegionName(
            String regionName,
            int size,
            long lastId
    ) {
        List<Content> contents = findContentsByRegion(regionName, lastId, size + EXTRA_FETCH_COUNT);

        // 실제 반환할 content는 size 만큼 잘라서 반환한다.
        List<Content> pagedContents = contents.stream()
                .limit(size)
                .toList();

        List<ContentDetailsByRegionResponse> contentDetails = getContentDetailsByRegionResponses(pagedContents);
        boolean loadable = contents.size() > size;

        return ContentsByRegionResponse.of(contentDetails, loadable);
    }

    private List<Content> findContentsByRegion(
            String regionName,
            long lastId,
            int sizePlusOne
    ) {
        Pageable pageable = PageRequest.of(0, sizePlusOne);
        boolean isFirstPage = lastId == 0;

        if (isFirstPage) {
            return contentRepository.findByRegionNameOrderByIdDesc(regionName, pageable);
        }
        return contentRepository.findByRegionNameAndIdLessThanOrderByIdDesc(regionName, lastId, pageable);
    }

    private List<ContentDetailsByRegionResponse> getContentDetailsByRegionResponses(List<Content> contents) {
        return contents.stream()
                .map(this::toContentDetailsResponse)
                .collect(Collectors.toList());
    }

    private ContentDetailsByRegionResponse toContentDetailsResponse(Content content) {
        ContentWithoutRegionResponse contentWithoutRegion = ContentWithoutRegionResponse.from(content);
        TripDurationResponse tripDuration = calculateTripDuration(content);
        int tripPlaceCount = tripCourseService.countByContentId(content.getId());

        return ContentDetailsByRegionResponse.of(contentWithoutRegion, tripDuration, tripPlaceCount);
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
