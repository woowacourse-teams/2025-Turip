package turip.favorite.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.ConflictException;
import turip.common.exception.custom.NotFoundException;
import turip.content.controller.dto.response.content.ContentDetailResponse;
import turip.content.controller.dto.response.content.ContentResponse;
import turip.content.controller.dto.response.content.ContentsDetailWithLoadableResponse;
import turip.content.controller.dto.response.content.TripDurationResponse;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.content.service.ContentPlaceService;
import turip.favorite.controller.dto.request.FavoriteContentRequest;
import turip.favorite.controller.dto.response.FavoriteContentResponse;
import turip.favorite.domain.FavoriteContent;
import turip.favorite.repository.FavoriteContentRepository;
import turip.member.domain.Member;

@Service
@RequiredArgsConstructor
public class FavoriteContentService {

    private final FavoriteContentRepository favoriteContentRepository;
    private final ContentRepository contentRepository;
    private final ContentPlaceService contentPlaceService;

    @Transactional
    public FavoriteContentResponse create(FavoriteContentRequest request, Member member) {
        Long contentId = request.contentId();
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.CONTENT_NOT_FOUND));
        if (favoriteContentRepository.existsByMemberIdAndContentId(member.getId(), content.getId())) {
            throw new ConflictException(ErrorTag.FAVORITE_CONTENT_CONFLICT);
        }
        FavoriteContent favoriteContent = new FavoriteContent(LocalDate.now(), member, content);
        FavoriteContent savedFavoriteContent = favoriteContentRepository.save(favoriteContent);
        return FavoriteContentResponse.from(savedFavoriteContent);
    }

    public ContentsDetailWithLoadableResponse findMyFavoriteContents(Member member, int pageSize, long lastContentId) {
        if (lastContentId == 0) {
            lastContentId = Long.MAX_VALUE;
        }
        Slice<Content> contentSlice = favoriteContentRepository.findMyFavoriteContentsByDeviceFid(member.getDeviceFid(),
                lastContentId,
                PageRequest.of(0, pageSize));
        List<Content> contents = contentSlice.getContent();
        List<ContentDetailResponse> contentsWithTripInfo = convertToContentWithTripInfoResponses(
                contents);
        boolean loadable = contentSlice.hasNext();

        return ContentsDetailWithLoadableResponse.of(contentsWithTripInfo, loadable);
    }

    @Transactional
    public void remove(Member member, Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.CONTENT_NOT_FOUND));
        FavoriteContent favoriteContent = favoriteContentRepository.findByMemberIdAndContentId(member.getId(),
                        content.getId())
                .orElseThrow(() -> new NotFoundException(ErrorTag.FAVORITE_CONTENT_NOT_FOUND));
        favoriteContentRepository.delete(favoriteContent);
    }

    private TripDurationResponse calculateTripDuration(Content content) {
        int totalTripDay = contentPlaceService.calculateDurationDays(content.getId());
        return TripDurationResponse.of(totalTripDay - 1, totalTripDay);
    }

    private List<ContentDetailResponse> convertToContentWithTripInfoResponses(List<Content> contents) {
        return contents.stream()
                .map(content -> {
                    ContentResponse contentWithCreatorAndCity = ContentResponse.of(content, true);
                    TripDurationResponse tripDuration = calculateTripDuration(content);
                    validateContentExists(content.getId());
                    int tripPlaceCount = contentPlaceService.countByContentId(content.getId());
                    return ContentDetailResponse.of(contentWithCreatorAndCity, tripDuration,
                            tripPlaceCount);
                })
                .toList();
    }

    private void validateContentExists(Long contentId) {
        boolean isContentExists = contentRepository.existsById(contentId);
        if (!isContentExists) {
            throw new NotFoundException(ErrorTag.CONTENT_NOT_FOUND);
        }
    }
}
