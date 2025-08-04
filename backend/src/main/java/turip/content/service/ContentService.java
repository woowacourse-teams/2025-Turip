package turip.content.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import turip.content.controller.dto.response.ContentByCityResponse;
import turip.content.controller.dto.response.ContentCountResponse;
import turip.content.controller.dto.response.ContentDetailsByRegionCategoryResponse;
import turip.content.controller.dto.response.ContentResponse;
import turip.content.controller.dto.response.ContentsByRegionCategoryResponse;
import turip.content.controller.dto.response.TripDurationResponse;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.exception.NotFoundException;
import turip.regioncategory.domain.DomesticRegionCategory;
import turip.regioncategory.domain.OverseasRegionCategory;
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

    public ContentsByRegionCategoryResponse getContentsByRegionCategory(
            String regionCategory,
            int size,
            long lastId
    ) {
        Slice<Content> contentSlice = findContentSlicesByRegionCategory(regionCategory, lastId, size);

        List<Content> contents = contentSlice.getContent();
        List<ContentDetailsByRegionCategoryResponse> contentDetails
                = convertContentsToContentDetailsByRegionResponses(contents);
        boolean loadable = contentSlice.hasNext();

        return ContentsByRegionCategoryResponse.of(contentDetails, loadable, regionCategory);
    }

    public ContentResponse getById(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("컨텐츠를 찾을 수 없습니다."));
        return ContentResponse.from(content);
    }

    private Slice<Content> findContentSlicesByRegionCategory(
            String regionCategory,
            long lastId,
            int size
    ) {
        Pageable pageable = PageRequest.of(0, size);
        boolean isFirstPage = lastId == 0;

        if ("국내 기타".equals(regionCategory)) {
            return findDomesticEtcContents(lastId, pageable, isFirstPage);
        }

        if ("해외 기타".equals(regionCategory)) {
            return findOverseasEtcContents(lastId, pageable, isFirstPage);
        }

        if (DomesticRegionCategory.containsName(regionCategory)) {
            return findContentsByCityName(regionCategory, lastId, pageable, isFirstPage);
        }

        return findContentsByCountryName(regionCategory, lastId, pageable, isFirstPage);
    }

    private Slice<Content> findDomesticEtcContents(long lastId, Pageable pageable, boolean isFirstPage) {
        List<String> domesticCategoryNames = DomesticRegionCategory.getDisplayNamesExcludingEtc();

        if (isFirstPage) {
            return contentRepository.findDomesticEtcContents(domesticCategoryNames, pageable);
        }
        return contentRepository.findDomesticEtcContentsWithLastId(domesticCategoryNames, lastId, pageable);
    }

    private Slice<Content> findOverseasEtcContents(long lastId, Pageable pageable, boolean isFirstPage) {
        List<String> overseasCategoryNames = OverseasRegionCategory.getDisplayNamesExcludingEtc();

        if (isFirstPage) {
            return contentRepository.findOverseasEtcContents(overseasCategoryNames, pageable);
        }
        return contentRepository.findOverseasEtcContentsWithLastId(overseasCategoryNames, lastId, pageable);
    }

    private Slice<Content> findContentsByCityName(String cityName, long lastId, Pageable pageable,
                                                  boolean isFirstPage) {
        if (isFirstPage) {
            return contentRepository.findByCityNameOrderByIdDesc(cityName, pageable);
        }
        return contentRepository.findByCityNameAndIdLessThanOrderByIdDesc(cityName, lastId, pageable);
    }

    private Slice<Content> findContentsByCountryName(String countryName, long lastId, Pageable pageable,
                                                     boolean isFirstPage) {
        if (isFirstPage) {
            return contentRepository.findByCityCountryNameOrderByIdDesc(countryName, pageable);
        }
        return contentRepository.findByCityCountryNameAndIdLessThanOrderByIdDesc(countryName, lastId, pageable);
    }

    private List<ContentDetailsByRegionCategoryResponse> convertContentsToContentDetailsByRegionResponses(
            List<Content> contents) {
        return contents.stream()
                .map(this::toContentDetailsByRegionResponse)
                .toList();
    }

    private ContentDetailsByRegionCategoryResponse toContentDetailsByRegionResponse(Content content) {
        ContentByCityResponse contentWithCity = ContentByCityResponse.from(content);
        TripDurationResponse tripDuration = calculateTripDuration(content);
        int tripPlaceCount = tripCourseService.countByContentId(content.getId());

        return ContentDetailsByRegionCategoryResponse.of(contentWithCity, tripDuration, tripPlaceCount);
    }

    private TripDurationResponse calculateTripDuration(Content content) {
        int totalTripDay = tripCourseService.calculateDurationDays(content.getId());
        return TripDurationResponse.of(totalTripDay - 1, totalTripDay);
    }
}
