package turip.controller.dto.response;

public record ContentApprovalResponse(
        Long contentId,
        Long contentPendingId
) {
}