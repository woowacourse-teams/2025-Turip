package turip.favorite.controller.dto.response;

public record FolderInvitationDetailResponse(Long turipId, boolean alreadyJoined) {

    public static FolderInvitationDetailResponse of(Long turipId, boolean alreadyJoined) {
        return new FolderInvitationDetailResponse(turipId, alreadyJoined);
    }
}
