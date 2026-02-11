package turip.favorite.controller.dto.response;

public record FolderInvitationDetailResponse(Long turipId) {

    public static FolderInvitationDetailResponse from(Long turipId) {
        return new FolderInvitationDetailResponse(turipId);
    }
}
