package turip.auth.service.dto;

import turip.account.domain.Member;

public record TuripLoginResult(TokenResult tokenResult, String nickname) {

    public static TuripLoginResult of(TokenResult tokenResult, Member member) {
        return new TuripLoginResult(tokenResult, member.getAccount().getNickname());
    }
}
