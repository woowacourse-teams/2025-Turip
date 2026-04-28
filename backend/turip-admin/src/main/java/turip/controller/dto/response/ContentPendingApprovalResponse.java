package turip.controller.dto.response;

import turip.content.domain.Content;
import turip.content.domain.ContentPending;

public record ContentPendingApprovalResponse(
        Long contentId,
        Long contentPendingId
) {

    public static ContentPendingApprovalResponse of(Content content, ContentPending contentPending) {
        return new ContentPendingApprovalResponse(content.getId(), contentPending.getId());
    }
}
