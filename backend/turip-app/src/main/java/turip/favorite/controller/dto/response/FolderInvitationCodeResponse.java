package turip.favorite.controller.dto.response;

public record FolderInvitationCodeResponse(String invitationCode) {

    public static FolderInvitationCodeResponse from(String invitationCode) {
        return new FolderInvitationCodeResponse(invitationCode);
    }
}
