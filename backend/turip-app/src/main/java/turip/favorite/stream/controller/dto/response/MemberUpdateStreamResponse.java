package turip.favorite.stream.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import turip.account.domain.Member;
import turip.favorite.domain.event.ActionType;

public record MemberUpdateStreamResponse(
        @JsonProperty("turipId") Long favoriteFolderId,
        @JsonProperty("action") ActionType actionType,
        int memberCount,
        List<MemberNicknameResponse> members,
        LocalDateTime timestamp
) {
    public static MemberUpdateStreamResponse of(Long favoriteFolderId, ActionType actionType, List<Member> members) {
        return new MemberUpdateStreamResponse(
                favoriteFolderId,
                actionType,
                members.size(),
                members.stream()
                        .map(member -> new MemberNicknameResponse(member.getAccount().getNickname()))
                        .toList(),
                LocalDateTime.now()
        );
    }

    public record MemberNicknameResponse(String nickname) {
    }
}
