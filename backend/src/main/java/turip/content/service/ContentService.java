package turip.content.service;

import static turip.region.domain.DomesticRegionCategory.OTHER_DOMESTIC;
import static turip.region.domain.OverseasRegionCategory.OTHER_OVERSEAS;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import turip.common.exception.custom.BadRequestException;
import turip.common.exception.custom.NotFoundException;
import turip.content.controller.dto.response.content.ContentCountResponse;
import turip.content.controller.dto.response.content.ContentDetailResponse;
import turip.content.controller.dto.response.content.ContentResponse;
import turip.content.controller.dto.response.content.ContentsDetailWithLoadableResponse;
import turip.content.controller.dto.response.content.TripDurationResponse;
import turip.content.controller.dto.response.favorite.WeeklyPopularFavoriteContentResponse;
import turip.content.controller.dto.response.favorite.WeeklyPopularFavoriteContentsResponse;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.favorite.repository.FavoriteContentRepository;
import turip.member.domain.Member;
import turip.region.domain.DomesticRegionCategory;
import turip.region.domain.OverseasRegionCategory;

@Service
@RequiredArgsConstructor
public class ContentService {

    private static final int DAYS_UNTIL_SUNDAY = 6;
    private static final int ONE_WEEK = 1;

    private final ContentRepository contentRepository;
    private final ContentPlaceService contentPlaceService;
    private final FavoriteContentRepository favoriteContentRepository;

    public ContentResponse getContentWithFavoriteStatus(Long contentId, Member member) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException("컨텐츠를 찾을 수 없습니다."));
        boolean isFavorite = favoriteContentRepository.existsByMemberIdAndContentId(member.getId(), content.getId());
        return ContentResponse.of(content, isFavorite);
    }

    public ContentCountResponse countByRegionCategory(String regionCategory) {
        int count = calculateCountByRegionCategory(regionCategory);
        return ContentCountResponse.from(count);
    }

    public ContentsDetailWithLoadableResponse searchContentsByKeyword(
            String keyword,
            int pageSize,
            long lastContentId
    ) {
        if (lastContentId == 0) {
            lastContentId = Long.MAX_VALUE;
        }
        Slice<Content> contentSlice = contentRepository.findByKeywordContaining(keyword, lastContentId,
                PageRequest.of(0, pageSize));
        List<Content> contents = contentSlice.getContent();
        List<ContentDetailResponse> contentWithTripDetailResponse = convertContentsToContentDetailResponse(contents);
        boolean loadable = contentSlice.hasNext();

        return ContentsDetailWithLoadableResponse.of(contentWithTripDetailResponse, loadable);
    }

    public ContentsDetailWithLoadableResponse findContentsByRegionCategory(
            String regionCategory,
            int size,
            long lastId
    ) {
        Slice<Content> contentSlice = findContentSlicesByRegionCategory(regionCategory, lastId, size);
        List<Content> contents = contentSlice.getContent();
        List<ContentDetailResponse> contentDetails = convertContentsToContentDetailResponse(contents);
        boolean loadable = contentSlice.hasNext();

        return ContentsDetailWithLoadableResponse.of(contentDetails, loadable);
    }

    public WeeklyPopularFavoriteContentsResponse findWeeklyPopularFavoriteContents(Member member, int topContentSize) {
        List<LocalDate> lastWeekPeriod = getLastWeekPeriod();
        LocalDate startDate = lastWeekPeriod.getFirst();
        LocalDate endDate = lastWeekPeriod.getLast();

        List<Content> popularContents = favoriteContentRepository.findPopularContentsByFavoriteBetweenDatesWithLimit(
                startDate, endDate, topContentSize);

        Set<Long> favoritedContentIds = findFavoritedContentIds(member, popularContents);

        return WeeklyPopularFavoriteContentsResponse.from(
                popularContents.stream()
                        .map(content -> WeeklyPopularFavoriteContentResponse.of(
                                content,
                                favoritedContentIds.contains(content.getId()),
                                calculateTripDuration(content)
                        ))
                        .toList()
        );
    }

    public ContentCountResponse countByKeyword(String keyword) {
        int count = contentRepository.countByKeywordContaining(keyword);
        return ContentCountResponse.from(count);
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
        if (OverseasRegionCategory.containsName(regionCategory)) {
            return contentRepository.countByCityCountryName(regionCategory);
        }
        throw new BadRequestException("지역 카테고리가 올바르지 않습니다.");
    }

    private int calculateDomesticEtcCount() {
        List<String> domesticCategoryNames = DomesticRegionCategory.getDisplayNamesExcludingEtc();
        return contentRepository.countDomesticEtcContents(domesticCategoryNames);
    }

    private int calculateOverseasEtcCount() {
        List<String> overseasCategoryNames = OverseasRegionCategory.getDisplayNamesExcludingEtc();
        return contentRepository.countOverseasEtcContents(overseasCategoryNames);
    }

    private Slice<Content> findContentSlicesByRegionCategory(
            String regionCategory,
            long lastId,
            int size
    ) {
        Pageable pageable = PageRequest.of(0, size);
        boolean isFirstPage = lastId == 0;

        if (OTHER_DOMESTIC.matchesDisplayName(regionCategory)) {
            return findDomesticEtcContents(lastId, pageable, isFirstPage);
        }
        if (OTHER_OVERSEAS.matchesDisplayName(regionCategory)) {
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

    private List<ContentDetailResponse> convertContentsToContentDetailResponse(List<Content> contents) {
        return contents.stream()
                .map(this::toContentDetailResponse)
                .toList();
    }

    private ContentDetailResponse toContentDetailResponse(Content content) {
        // TODO: 찜 여부 조회
        ContentResponse contentResponse = ContentResponse.of(content, false);
        TripDurationResponse tripDuration = calculateTripDuration(content);

        int tripPlaceCount = getTripPlaceCount(content);

        return ContentDetailResponse.of(contentResponse, tripDuration, tripPlaceCount);
    }

    private int getTripPlaceCount(Content content) {
        boolean isContentExists = contentRepository.existsById(content.getId());
        if (!isContentExists) {
            throw new NotFoundException("컨텐츠를 찾을 수 없습니다.");
        }
        return contentPlaceService.countByContentId(content.getId());
    }

    private TripDurationResponse calculateTripDuration(Content content) {
        int totalTripDay = contentPlaceService.calculateDurationDays(content.getId());
        return TripDurationResponse.of(totalTripDay - 1, totalTripDay);
    }

    private List<LocalDate> getLastWeekPeriod() {
        LocalDate thisWeekMonday = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate lastWeekMonday = thisWeekMonday.minusWeeks(ONE_WEEK);
        LocalDate lastWeekSunday = lastWeekMonday.plusDays(DAYS_UNTIL_SUNDAY);
        return new ArrayList<>(List.of(lastWeekMonday, lastWeekSunday));
    }

    private Set<Long> findFavoritedContentIds(Member member, List<Content> contents) {
        List<Long> contentIds = contents.stream()
                .map(Content::getId)
                .toList();
        return favoriteContentRepository.findByMemberIdAndContentIdIn(member.getId(), contentIds).stream()
                .map(favorite -> favorite.getContent().getId())
                .collect(Collectors.toSet());
    }
}
