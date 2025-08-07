package turip.favorite.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.content.controller.dto.response.ContentWithCreatorAndCityResponse;
import turip.content.controller.dto.response.ContentWithTripInfoResponse;
import turip.content.controller.dto.response.MyFavoriteContentsResponse;
import turip.content.controller.dto.response.TripDurationResponse;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.exception.custom.BadRequestException;
import turip.exception.custom.NotFoundException;
import turip.favorite.controller.dto.request.FavoriteRequest;
import turip.favorite.controller.dto.response.FavoriteResponse;
import turip.favorite.domain.Favorite;
import turip.favorite.repository.FavoriteRepository;
import turip.member.domain.Member;
import turip.member.repository.MemberRepository;
import turip.tripcourse.service.TripCourseService;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;
    private final ContentRepository contentRepository;
    private final TripCourseService tripCourseService;

    @Transactional
    public FavoriteResponse create(FavoriteRequest request, String deviceFid) {
        Long contentId = request.contentId();
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 컨텐츠입니다."));
        Member member = findOrCreateMember(deviceFid);
        if (favoriteRepository.existsByMemberIdAndContentId(member.getId(), content.getId())) {
            throw new BadRequestException("이미 찜한 컨텐츠입니다.");
        }
        Favorite favorite = new Favorite(LocalDate.now(), member, content);
        Favorite savedFavorite = favoriteRepository.save(favorite);
        return FavoriteResponse.from(savedFavorite);
    }

    public MyFavoriteContentsResponse findMyFavoriteContents(String deviceFid, int pageSize, long lastContentId) {
        if (deviceFid == null) {
            throw new BadRequestException("사용자 정보를 조회할 수 없습니다.");
        }
        if (lastContentId == 0) {
            lastContentId = Long.MAX_VALUE;
        }
        Slice<Content> contentSlice = favoriteRepository.findMyFavoriteContentsByDeviceFid(deviceFid, lastContentId,
                PageRequest.of(0, pageSize));
        List<Content> contents = contentSlice.getContent();
        List<ContentWithTripInfoResponse> contentsWithTripInfo = convertToContentWithTripInfoResponses(contents);
        boolean loadable = contentSlice.hasNext();

        return MyFavoriteContentsResponse.of(contentsWithTripInfo, loadable);
    }

    @Transactional
    public void remove(String deviceFid, Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 컨텐츠입니다."));
        Member member = memberRepository.findByDeviceFid(deviceFid)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        Favorite favorite = favoriteRepository.findByMemberIdAndContentId(member.getId(), content.getId())
                .orElseThrow(() -> new NotFoundException("해당 컨텐츠는 찜한 상태가 아닙니다."));
        favoriteRepository.delete(favorite);
    }


    private Member findOrCreateMember(String deviceFid) {
        return memberRepository.findByDeviceFid(deviceFid)
                .orElseGet(() -> memberRepository.save(new Member(deviceFid)));
    }

    private TripDurationResponse calculateTripDuration(Content content) {
        int totalTripDay = tripCourseService.calculateDurationDays(content.getId());
        return TripDurationResponse.of(totalTripDay - 1, totalTripDay);
    }

    private List<ContentWithTripInfoResponse> convertToContentWithTripInfoResponses(List<Content> contents) {
        return contents.stream()
                .map(content -> {
                    ContentWithCreatorAndCityResponse contentWithCreatorAndCity = ContentWithCreatorAndCityResponse.from(
                            content);
                    TripDurationResponse tripDuration = calculateTripDuration(content);
                    int tripPlaceCount = tripCourseService.countByContentId(content.getId());
                    return ContentWithTripInfoResponse.of(contentWithCreatorAndCity, tripDuration, tripPlaceCount);
                })
                .toList();
    }
}
