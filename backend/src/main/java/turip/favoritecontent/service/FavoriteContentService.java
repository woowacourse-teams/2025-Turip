package turip.favoritecontent.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.content.controller.dto.response.ContentResponse;
import turip.content.controller.dto.response.ContentWithTripInfoAndFavoriteResponse;
import turip.content.controller.dto.response.MyFavoriteContentsResponse;
import turip.content.controller.dto.response.TripDurationResponse;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.exception.custom.BadRequestException;
import turip.exception.custom.NotFoundException;
import turip.favoritecontent.controller.dto.request.FavoriteContentRequest;
import turip.favoritecontent.controller.dto.response.FavoriteContentResponse;
import turip.favoritecontent.domain.FavoriteContent;
import turip.favoritecontent.repository.FavoriteContentRepository;
import turip.member.domain.Member;
import turip.member.repository.MemberRepository;
import turip.tripcourse.service.TripCourseService;

@Service
@RequiredArgsConstructor
public class FavoriteContentService {

    private final FavoriteContentRepository favoriteContentRepository;
    private final MemberRepository memberRepository;
    private final ContentRepository contentRepository;
    private final TripCourseService tripCourseService;

    @Transactional
    public FavoriteContentResponse create(FavoriteContentRequest request, String deviceFid) {
        Long contentId = request.contentId();
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 컨텐츠입니다."));
        Member member = findOrCreateMember(deviceFid);
        if (favoriteContentRepository.existsByMemberIdAndContentId(member.getId(), content.getId())) {
            throw new BadRequestException("이미 찜한 컨텐츠입니다.");
        }
        FavoriteContent favoriteContent = new FavoriteContent(LocalDate.now(), member, content);
        FavoriteContent savedFavoriteContent = favoriteContentRepository.save(favoriteContent);
        return FavoriteContentResponse.from(savedFavoriteContent);
    }

    public MyFavoriteContentsResponse findMyFavoriteContents(String deviceFid, int pageSize, long lastContentId) {
        if (deviceFid == null) {
            throw new BadRequestException("사용자 정보를 조회할 수 없습니다.");
        }
        if (lastContentId == 0) {
            lastContentId = Long.MAX_VALUE;
        }
        Slice<Content> contentSlice = favoriteContentRepository.findMyFavoriteContentsByDeviceFid(deviceFid,
                lastContentId,
                PageRequest.of(0, pageSize));
        List<Content> contents = contentSlice.getContent();
        List<ContentWithTripInfoAndFavoriteResponse> contentsWithTripInfo = convertToContentWithTripInfoResponses(
                contents);
        boolean loadable = contentSlice.hasNext();

        return MyFavoriteContentsResponse.of(contentsWithTripInfo, loadable);
    }

    @Transactional
    public void remove(String deviceFid, Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 컨텐츠입니다."));
        Member member = memberRepository.findByDeviceFid(deviceFid)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        FavoriteContent favoriteContent = favoriteContentRepository.findByMemberIdAndContentId(member.getId(),
                        content.getId())
                .orElseThrow(() -> new NotFoundException("해당 컨텐츠는 찜한 상태가 아닙니다."));
        favoriteContentRepository.delete(favoriteContent);
    }

    private Member findOrCreateMember(String deviceFid) {
        return memberRepository.findByDeviceFid(deviceFid)
                .orElseGet(() -> memberRepository.save(new Member(deviceFid)));
    }

    private TripDurationResponse calculateTripDuration(Content content) {
        int totalTripDay = tripCourseService.calculateDurationDays(content.getId());
        return TripDurationResponse.of(totalTripDay - 1, totalTripDay);
    }

    private List<ContentWithTripInfoAndFavoriteResponse> convertToContentWithTripInfoResponses(List<Content> contents) {
        return contents.stream()
                .map(content -> {
                    ContentResponse contentWithCreatorAndCity = ContentResponse.of(content, true);
                    TripDurationResponse tripDuration = calculateTripDuration(content);
                    int tripPlaceCount = tripCourseService.countByContentId(content.getId());
                    return ContentWithTripInfoAndFavoriteResponse.of(contentWithCreatorAndCity, tripDuration,
                            tripPlaceCount);
                })
                .toList();
    }
}
