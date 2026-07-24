package turip.controller.dto.response;

import java.time.LocalDateTime;
import turip.account.controller.dto.response.AccountResponse;
import turip.content.domain.ContentPending;
import turip.content.domain.ContentPendingData;
import turip.content.domain.ContentPendingStatus;

public record ContentPendingResponse(
        Long id,
        ContentPendingData contentData,
        ContentPendingStatus status,
        AccountResponse collectorAccount,
        AccountResponse validatorAccount,
        String rejectReason,
        Long contentId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime contentDataUpdatedAt
) {

    public static ContentPendingResponse from(ContentPending contentPending) {
        return new ContentPendingResponse(
                contentPending.getId(),
                contentPending.getContentData(),
                contentPending.getStatus(),
                AccountResponse.from(contentPending.getCollectorAccount()),
                contentPending.getValidatorAccount() != null
                        ? AccountResponse.from(contentPending.getValidatorAccount())
                        : null,
                contentPending.getRejectReason(),
                contentPending.getContent() != null ? contentPending.getContent().getId() : null,
                contentPending.getCreatedAt(),
                contentPending.getUpdatedAt(),
                contentPending.getContentDataUpdatedAt()
        );
    }
}
