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
import turip.controller.dto.request.ContentPendingRejectRequest;
import turip.controller.dto.response.ContentPendingApprovalResponse;
import turip.controller.dto.response.ContentPendingRejectResponse;
import turip.controller.dto.response.ContentPendingResponse;

@Service
@RequiredArgsConstructor
public class AdminContentPendingService {

    private final ContentPendingRepository contentPendingRepository;
    private final ContentRepository contentRepository;
    private final AdminContentService adminContentService;

    @Transactional
    public ContentPendingResponse findById(Long id) {
        ContentPending contentPending = getById(id);
        return ContentPendingResponse.from(contentPending);
    }

    @Transactional
    public ContentPendingApprovalResponse approve(Long contentPendingId, Account validatorAccount) {
        ContentPending contentPending = getById(contentPendingId);
        ContentPendingData data = contentPending.getContentData();

        AdminContentSaveRequest request = convertToSaveRequest(data);
        Long contentId = adminContentService.save(request);

        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.CONTENT_NOT_FOUND));
        contentPending.approve(validatorAccount, content);

        return new ContentPendingApprovalResponse(content.getId(), contentPending.getId());
    }

    @Transactional
    public ContentPendingRejectResponse reject(
            Long contentPendingId,
            Account validatorAccount,
            ContentPendingRejectRequest request
    ) {
        ContentPending contentPending = getById(contentPendingId);
        String rejectReason = request.rejectReason();
        contentPending.reject(validatorAccount, rejectReason);
        return ContentPendingRejectResponse.from(contentPendingId);
    }

    private ContentPending getById(Long id) {
        return contentPendingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorTag.CONTENT_PENDING_NOT_FOUND));
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
