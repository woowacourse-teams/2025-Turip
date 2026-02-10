package turip.favorite.controller.dto.response;

import turip.account.domain.Member;

public record FavoriteFolderMemberResponse(String email, String nickname) {

    public static FavoriteFolderMemberResponse from(Member member) {
        return new FavoriteFolderMemberResponse(member.getEmail(), member.getAccount().getNickname());
    }
}
