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
import turip.common.exception.ErrorTag;
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
                .orElseThrow(() -> new NotFoundException(ErrorTag.CONTENT_NOT_FOUND));
        boolean isFavorite = favoriteContentRepository.existsByMemberIdAndContentId(member.getId(), content.getId());
        return ContentResponse.of(content, isFavorite);
    }

    public ContentCountResponse countByRegionCategory(String regionCategory) {
        int count = calculateCountByRegionCategory(regionCategory);
        return ContentCountResponse.from(count);
    }

    public ContentsDetailWithLoadableResponse searchContentsByKeyword(
            Member member,
            String keyword,
            int pageSize,
            long lastContentId
    ) {
        if (lastContentId == 0) {
            lastContentId = Long.MAX_VALUE;
        }
        Slice<Content> contentSlice = contentRepository.findByKeywordContaining(keyword, lastContentId,
                PageRequest.of(0, pageSize));
        return converToContentsDetailWithLoadableResponse(member, contentSlice);
    }

    public ContentsDetailWithLoadableResponse findContentsByRegionCategory(
            Member member,
            String regionCategory,
            int size,
            long lastId
    ) {
        Slice<Content> contentSlice = findContentSlicesByRegionCategory(regionCategory, lastId, size);
        return converToContentsDetailWithLoadableResponse(member, contentSlice);
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
        throw new BadRequestException(ErrorTag.REGION_CATEGORY_INVALID);
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
        if (lastId == 0) {
            lastId = Long.MAX_VALUE;
        }

        if (OTHER_DOMESTIC.matchesDisplayName(regionCategory)) {
            List<String> domesticCategoryNames = DomesticRegionCategory.getDisplayNamesExcludingEtc();
            return contentRepository.findDomesticEtcContents(domesticCategoryNames, lastId, pageable);
        }
        if (OTHER_OVERSEAS.matchesDisplayName(regionCategory)) {
            List<String> overseasCategoryNames = OverseasRegionCategory.getDisplayNamesExcludingEtc();
            return contentRepository.findOverseasEtcContents(overseasCategoryNames, lastId, pageable);
        }
        if (DomesticRegionCategory.containsName(regionCategory)) {
            return contentRepository.findByCityName(regionCategory, lastId, pageable);
        }
        if (OverseasRegionCategory.containsName(regionCategory)) {
            return contentRepository.findByCityCountryName(regionCategory, lastId, pageable);
        }
        throw new BadRequestException(ErrorTag.REGION_CATEGORY_INVALID);
    }

    private Set<Long> findFavoritedContentIds(Member member, List<Content> contents) {
        List<Long> contentIds = contents.stream()
                .map(Content::getId)
                .toList();
        return favoriteContentRepository.findByMemberIdAndContentIdIn(member.getId(), contentIds).stream()
                .map(favorite -> favorite.getContent().getId())
                .collect(Collectors.toSet());
    }

    private ContentsDetailWithLoadableResponse converToContentsDetailWithLoadableResponse(Member member,
                                                                                          Slice<Content> contentSlice) {
        List<Content> contents = contentSlice.getContent();
        Set<Long> favoritedContentIds = findFavoritedContentIds(member, contents);
        List<ContentDetailResponse> contentDetails = contents.stream()
                .map(content -> toContentDetailResponse(content, favoritedContentIds.contains(content.getId())))
                .toList();
        boolean loadable = contentSlice.hasNext();
        return ContentsDetailWithLoadableResponse.of(contentDetails, loadable);
    }

    private ContentDetailResponse toContentDetailResponse(Content content, boolean isFavorite) {
        ContentResponse contentResponse = ContentResponse.of(content, isFavorite);
        TripDurationResponse tripDuration = calculateTripDuration(content);
        int tripPlaceCount = getTripPlaceCount(content);
        return ContentDetailResponse.of(contentResponse, tripDuration, tripPlaceCount);
    }

    private TripDurationResponse calculateTripDuration(Content content) {
        int totalTripDay = contentPlaceService.calculateDurationDays(content.getId());
        return TripDurationResponse.of(totalTripDay - 1, totalTripDay);
    }

    private int getTripPlaceCount(Content content) {
        boolean isContentExists = contentRepository.existsById(content.getId());
        if (!isContentExists) {
            throw new NotFoundException(ErrorTag.CONTENT_NOT_FOUND);
        }
        return contentPlaceService.countByContentId(content.getId());
    }

    private List<LocalDate> getLastWeekPeriod() {
        LocalDate thisWeekMonday = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate lastWeekMonday = thisWeekMonday.minusWeeks(ONE_WEEK);
        LocalDate lastWeekSunday = lastWeekMonday.plusDays(DAYS_UNTIL_SUNDAY);
        return new ArrayList<>(List.of(lastWeekMonday, lastWeekSunday));
    }
}
