package turip.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.Account;
import turip.account.domain.Role;
import turip.account.repository.AccountRepository;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.ConflictException;
import turip.common.exception.custom.NotFoundException;
import turip.content.domain.Content;
import turip.content.domain.ContentPending;
import turip.content.domain.ContentPendingData;
import turip.content.domain.ContentPendingStatus;
import turip.content.repository.ContentPendingRepository;
import turip.content.repository.ContentRepository;
import turip.controller.dto.request.AdminContentSaveRequest;
import turip.controller.dto.request.AdminContentSaveRequest.ContentPlaceRequest;
import turip.controller.dto.request.AdminContentSaveRequest.VideoRequest;
import turip.controller.dto.request.ContentPendingRejectRequest;
import turip.controller.dto.response.ContentPendingApprovalResponse;
import turip.controller.dto.response.ContentPendingRejectResponse;
import turip.controller.dto.response.ContentPendingResponse;
import turip.controller.dto.response.MyCollectContentResponse;
import turip.controller.dto.response.PendingListResponse;

@Service
@RequiredArgsConstructor
public class AdminContentPendingService {

    private final ContentPendingRepository contentPendingRepository;
    private final ContentRepository contentRepository;
    private final AccountRepository accountRepository;
    private final AdminContentService adminContentService;

    @Transactional
    public ContentPendingResponse findById(Long id) {
        ContentPending contentPending = getById(id);
        return ContentPendingResponse.from(contentPending);
    }

    public List<PendingListResponse> findAll() {
        return contentPendingRepository.findAllByStatusOrderByIdDesc(ContentPendingStatus.PENDING)
                .stream()
                .map(PendingListResponse::from)
                .toList();
    }

    public List<MyCollectContentResponse> getMyHistory(Account account) {
        return contentPendingRepository.findAllByCollectorAccountOrderByIdDesc(account)
                .stream()
                .map(MyCollectContentResponse::from)
                .toList();
    }

    @Transactional
    public Long pending(Account collectorAccount, ContentPendingData contentData) {
        validateDuplicatePending(contentData.video().url());
        Account validatorAccount = determineValidator(collectorAccount);
        ContentPending contentPending = new ContentPending(contentData, collectorAccount, validatorAccount, null, null);
        return contentPendingRepository.save(contentPending).getId();
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

        return ContentPendingApprovalResponse.of(content, contentPending);
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

    private void validateDuplicatePending(String videoUrl) {
        if (contentPendingRepository.existsByVideoUrlAndStatus(videoUrl, ContentPendingStatus.PENDING)) {
            throw new ConflictException(ErrorTag.DUPLICATE_PENDING_CONTENT);
        }
    }

    private Account determineValidator(Account collectorAccount) {
        List<Account> candidates = accountRepository.findAllByRole(Role.ADMIN)
                .stream()
                .filter(a -> !a.getId().equals(collectorAccount.getId()))
                .toList();

        if (candidates.isEmpty()) {
            throw new NotFoundException(ErrorTag.NO_AVAILABLE_VALIDATOR);
        }

        Optional<ContentPending> lastPending = contentPendingRepository.findTopByOrderByIdDesc();
        if (lastPending.isEmpty()) {
            return candidates.get(0);
        }

        Account lastValidator = lastPending.get().getValidatorAccount();
        if (lastValidator == null) {
            return candidates.get(0);
        }

        Long lastValidatorId = lastValidator.getId();
        for (int i = 0; i < candidates.size(); i++) {
            if (candidates.get(i).getId().equals(lastValidatorId)) {
                return candidates.get((i + 1) % candidates.size());
            }
        }
        return candidates.get(0);
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
