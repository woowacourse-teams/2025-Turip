package turip.favorite.controller.dto.response;

public record FolderInvitationDetailResponse(Long turipId, boolean alreadyJoined) {

    public static FolderInvitationDetailResponse from(Long turipId, boolean alreadyJoined) {
        return new FolderInvitationDetailResponse(turipId, alreadyJoined);
    }
}
