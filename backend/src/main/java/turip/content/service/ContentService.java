package turip.content.service;

import static turip.regioncategory.domain.DomesticRegionCategory.OTHER_DOMESTIC;
import static turip.regioncategory.domain.OverseasRegionCategory.OTHER_OVERSEAS;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import turip.content.controller.dto.response.ContentCountResponse;
import turip.content.controller.dto.response.ContentDetailsByCityResponse;
import turip.content.controller.dto.response.ContentResponse;
import turip.content.controller.dto.response.ContentSearchResponse;
import turip.content.controller.dto.response.ContentSearchResultResponse;
import turip.content.controller.dto.response.ContentWithCreatorAndCityResponse;
import turip.content.controller.dto.response.ContentWithoutCityResponse;
import turip.content.controller.dto.response.ContentsByCityResponse;
import turip.content.controller.dto.response.TripDurationResponse;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.exception.NotFoundException;
import turip.favorite.repository.FavoriteRepository;
import turip.member.repository.MemberRepository;
import turip.regioncategory.domain.DomesticRegionCategory;
import turip.regioncategory.domain.OverseasRegionCategory;
import turip.tripcourse.service.TripCourseService;

@Service
@RequiredArgsConstructor
public class ContentService {

    private static final int EXTRA_FETCH_COUNT = 1;

    private final ContentRepository contentRepository;
    private final TripCourseService tripCourseService;
    private final MemberRepository memberRepository;
    private final FavoriteRepository favoriteRepository;

    public ContentResponse getContentWithFavoriteStatus(Long contentId, String deviceFid) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException("컨텐츠를 찾을 수 없습니다."));
        if (deviceFid == null) {
            return ContentResponse.of(content, false);
        }
        boolean isFavorite = memberRepository.findByDeviceFid(deviceFid)
                .map(member -> favoriteRepository.existsByMemberIdAndContentId(member.getId(), content.getId()))
                .orElse(false);

        return ContentResponse.of(content, isFavorite);
    }
  
    public ContentCountResponse countByRegionCategory(String regionCategory) {
        int count = calculateCountByRegionCategory(regionCategory);
        return ContentCountResponse.from(count);
    }

    public ContentsByCityResponse findContentsByCityName(
            String cityName,
            int size,
            long lastId
    ) {
        List<Content> contents = findContentsByCity(cityName, lastId, size + EXTRA_FETCH_COUNT);

        // 실제 반환할 content는 size 만큼 잘라서 반환한다.
        List<Content> pagedContents = contents.stream()
                .limit(size)
                .toList();

        List<ContentDetailsByCityResponse> contentDetails
                = convertContentsToContentDetailsByRegionResponses(pagedContents);
        boolean loadable = contents.size() > size;

        return ContentsByCityResponse.of(contentDetails, loadable);
    }

    public ContentCountResponse countByCityName(String cityName) {
        int count = contentRepository.countByCityName(cityName);
        return ContentCountResponse.from(count);
    }

    public ContentCountResponse countByKeyword(String keyword) {
        int count = contentRepository.countByKeywordContaining(keyword);
        return ContentCountResponse.from(count);
    }

    public ContentSearchResponse searchContentsByKeyword(
            String keyword,
            int pageSize,
            long lastContentId
    ) {
        if (lastContentId == 0) {
            lastContentId = Long.MAX_VALUE;
        }
        Slice<Content> contents = contentRepository.findByKeywordContaining(keyword, lastContentId,
                PageRequest.of(0, pageSize));
        boolean loadable = contents.hasNext();

        List<ContentSearchResultResponse> contentSearchResultResponses = convertContentsToContentSearchResultResponse(
                contents);

        return ContentSearchResponse.of(contentSearchResultResponses, loadable);
    }

    public ContentResponse getById(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("컨텐츠를 찾을 수 없습니다."));
        return ContentResponse.from(content);
    }

    private int calculateCountByRegionCategory(String regionCategory) {
        if (OTHER_DOMESTIC.matchesDisplayName(regionCategory)) {
            return calculateDomesticEtcCount();
        }

        if (OTHER_OVERSEAS.matchesDisplayName(regionCategory)) {
            return calculateOverseasEtcCount();
        }

        if (DomesticRegionCategory.containsName(regionCategory)) {
            return contentRepository.countByCityName(regionCategory);
        }

        return contentRepository.countByCityCountryName(regionCategory);
    }

    private int calculateDomesticEtcCount() {
        List<String> domesticCategoryNames = DomesticRegionCategory.getDisplayNamesExcludingEtc();
        return contentRepository.countByCityNameNotIn(domesticCategoryNames);
    }

    private int calculateOverseasEtcCount() {
        List<String> overseasCategoryNames = OverseasRegionCategory.getDisplayNamesExcludingEtc();
        return contentRepository.countByCountryNameNotIn(overseasCategoryNames);
    }

    private List<Content> findContentsByCity(
            String cityName,
            long lastId,
            int sizePlusOne
    ) {
        Pageable pageable = PageRequest.of(0, sizePlusOne);
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

    private List<ContentSearchResultResponse> convertContentsToContentSearchResultResponse(Slice<Content> contents) {
        return contents.stream()
                .map(this::toContentSearchResultResponse)
                .toList();
    }

    private ContentDetailsByCityResponse toContentDetailsResponse(Content content) {
        ContentWithoutCityResponse contentWithoutRegion = ContentWithoutCityResponse.from(content);
        TripDurationResponse tripDuration = calculateTripDuration(content);
        int tripPlaceCount = tripCourseService.countByContentId(content.getId());

        return ContentDetailsByCityResponse.of(contentWithoutRegion, tripDuration, tripPlaceCount);
    }

    private ContentSearchResultResponse toContentSearchResultResponse(Content content) {
        int placeCount = tripCourseService.countByContentId(content.getId());

        return ContentSearchResultResponse.of(
                ContentWithCreatorAndCityResponse.from(content),
                calculateTripDuration(content),
                placeCount
        );
    }

    private TripDurationResponse calculateTripDuration(Content content) {
        int totalTripDay = tripCourseService.calculateDurationDays(content.getId());
        return TripDurationResponse.of(totalTripDay - 1, totalTripDay);
    }
}
