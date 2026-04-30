package turip.controller.dto.response;

public record ContentPendingRejectResponse(Long contentPendingId) {

    public static ContentPendingRejectResponse from(Long contentPendingId) {
        return new ContentPendingRejectResponse(contentPendingId);
    }
}
