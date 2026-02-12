package turip.account.controller.dto.response;

import turip.account.domain.TuripMember;

public record TuripMemberResponse(Long id, Long memberId, String loginId) {

    public static TuripMemberResponse from(TuripMember turipMember) {
        return new TuripMemberResponse(turipMember.getId(), turipMember.getMember().getId(), turipMember.getLoginId());
    }
}
