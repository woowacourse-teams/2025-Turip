package turip.content.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import turip.favorite.repository.FavoriteRepository;
import turip.member.repository.MemberRepository;
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
        boolean isFavorite = false;
        if (deviceFid != null) {
            isFavorite = memberRepository.findByDeviceFid(deviceFid)
                    .map(member -> favoriteRepository.existsByMemberIdAndContentId(member.getId(), content.getId()))
                    .orElse(false);
        }

        return ContentResponse.of(content, isFavorite);
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
}
