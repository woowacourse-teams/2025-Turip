package turip.favorite.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.Account;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.ConflictException;
import turip.common.exception.custom.NotFoundException;
import turip.content.controller.dto.response.content.TripDurationResponse;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.content.service.ContentPlaceService;
import turip.favorite.controller.dto.request.FavoriteContentRequest;
import turip.favorite.controller.dto.response.FavoriteContentCountResponse;
import turip.favorite.controller.dto.response.FavoriteContentDetailResponse;
import turip.favorite.controller.dto.response.FavoriteContentResponse;
import turip.favorite.domain.FavoriteContent;
import turip.favorite.repository.FavoriteContentRepository;
import turip.favorite.service.dto.FavoriteContentWithLoadableResult;

@Service
@RequiredArgsConstructor
public class FavoriteContentService {

    private final FavoriteContentRepository favoriteContentRepository;
    private final ContentRepository contentRepository;
    private final ContentPlaceService contentPlaceService;

    @Transactional
    public FavoriteContentResponse create(FavoriteContentRequest request, Account account) {
        Long contentId = request.contentId();
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.CONTENT_NOT_FOUND));
        if (favoriteContentRepository.existsByAccountIdAndContentId(account.getId(), content.getId())) {
            throw new ConflictException(ErrorTag.FAVORITE_CONTENT_CONFLICT);
        }
        FavoriteContent favoriteContent = new FavoriteContent(LocalDate.now(), account, content);
        FavoriteContent savedFavoriteContent = favoriteContentRepository.save(favoriteContent);
        return FavoriteContentResponse.from(savedFavoriteContent);
    }

    public FavoriteContentWithLoadableResult findMyFavoriteContents(Account account, int pageSize,
                                                                    long lastFavoriteContentId) {
        if (lastFavoriteContentId == 0) {
            lastFavoriteContentId = Long.MAX_VALUE;
        }
        Slice<FavoriteContent> favoriteContentSlice = favoriteContentRepository.findMyFavoriteContentsByAccountId(
                account.getId(),
                lastFavoriteContentId,
                PageRequest.of(0, pageSize));
        List<FavoriteContent> contents = favoriteContentSlice.getContent();
        List<FavoriteContentDetailResponse> favoriteContentDetailResponse = convertToFavoriteContentDetailResponses(
                contents);
        boolean loadable = favoriteContentSlice.hasNext();

        return FavoriteContentWithLoadableResult.of(favoriteContentDetailResponse, loadable);
    }

    public boolean existsByAccount(Account account) {
        return favoriteContentRepository.existsByAccount(account);
    }

    public FavoriteContentCountResponse countByAccount(Account account) {
        int count = favoriteContentRepository.countByAccount(account);
        return FavoriteContentCountResponse.from(count);
    }

    @Transactional
    public void remove(Account account, Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.CONTENT_NOT_FOUND));
        FavoriteContent favoriteContent = favoriteContentRepository.findByAccountIdAndContentId(account.getId(),
                        content.getId())
                .orElseThrow(() -> new NotFoundException(ErrorTag.FAVORITE_CONTENT_NOT_FOUND));
        favoriteContentRepository.delete(favoriteContent);
    }

    private TripDurationResponse calculateTripDuration(Content content) {
        int totalTripDay = contentPlaceService.calculateDurationDays(content.getId());
        return TripDurationResponse.of(totalTripDay - 1, totalTripDay);
    }

    private List<FavoriteContentDetailResponse> convertToFavoriteContentDetailResponses(
            List<FavoriteContent> favoriteContents) {
        return favoriteContents.stream()
                .map(favoriteContent -> {
                    Content content = favoriteContent.getContent();
                    validateContentExists(content.getId());
                    TripDurationResponse tripDuration = calculateTripDuration(content);
                    int tripPlaceCount = contentPlaceService.countByContentId(content.getId());
                    return FavoriteContentDetailResponse.of(favoriteContent, tripDuration, tripPlaceCount);
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
