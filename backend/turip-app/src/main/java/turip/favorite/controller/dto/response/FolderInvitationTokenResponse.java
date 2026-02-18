package turip.favorite.controller.dto.response;

public record FolderInvitationTokenResponse(String invitationCode) {

    public static FolderInvitationTokenResponse from(String invitationCode) {
        return new FolderInvitationTokenResponse(invitationCode);
    }
}
