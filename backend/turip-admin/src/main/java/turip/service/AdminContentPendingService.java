package turip.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.Account;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.NotFoundException;
import turip.content.domain.Content;
import turip.content.domain.ContentPending;
import turip.content.domain.ContentPendingData;
import turip.content.repository.ContentPendingRepository;
import turip.content.repository.ContentRepository;
import turip.controller.dto.request.AdminContentSaveRequest;
import turip.controller.dto.request.AdminContentSaveRequest.ContentPlaceRequest;
import turip.controller.dto.request.AdminContentSaveRequest.VideoRequest;
import turip.controller.dto.response.ContentPendingApprovalResponse;

@Service
@RequiredArgsConstructor
public class AdminContentPendingService {

    private final ContentPendingRepository contentPendingRepository;
    private final ContentRepository contentRepository;
    private final AdminContentService adminContentService;

    @Transactional
    public ContentPending findById(Long id) {
        return contentPendingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorTag.CONTENT_PENDING_NOT_FOUND));
    }

    @Transactional
    public ContentPendingApprovalResponse approve(Long contentPendingId, Account validatorAccount) {
        ContentPending contentPending = findById(contentPendingId);
        ContentPendingData data = contentPending.getContentData();

        AdminContentSaveRequest request = convertToSaveRequest(data);
        Long contentId = adminContentService.save(request);

        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.CONTENT_NOT_FOUND));
        contentPending.approve(validatorAccount, content);

        return new ContentPendingApprovalResponse(content.getId(), contentPending.getId());
    }

    private AdminContentSaveRequest convertToSaveRequest(ContentPendingData data) {
        VideoRequest videoRequest = VideoRequest.from(data);
        List<ContentPlaceRequest> contentPlaces = data.contentPlaces().stream()
                .map(ContentPlaceRequest::from)
                .toList();

        return AdminContentSaveRequest.of(
                data.cityName(),
                videoRequest,
                null,
                contentPlaces
        );
    }
}
