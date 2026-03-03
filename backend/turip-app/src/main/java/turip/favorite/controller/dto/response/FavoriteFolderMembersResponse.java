package turip.favorite.controller.dto.response;

import java.util.List;
import turip.account.domain.Member;

public record FavoriteFolderMembersResponse(List<FavoriteFolderMemberResponse> members) {

    public static FavoriteFolderMembersResponse of(List<Member> members) {
        List<FavoriteFolderMemberResponse> favoriteFolderMembers = members.stream()
                .map(FavoriteFolderMemberResponse::from).toList();
        return new FavoriteFolderMembersResponse(favoriteFolderMembers);
    }
}
