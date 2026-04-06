package turip.auth.service.dto;


import turip.account.domain.Member;

public record SocialLoginResult(
        String accessToken,
        String refreshToken,
        boolean isNewMember,
        String nickname,
        boolean isMigrationDecided
) {

    public static SocialLoginResult of(
            TokenResult tokenResult,
            boolean isNewMember,
            Member member
    ) {
        return new SocialLoginResult(
                tokenResult.accessToken(),
                tokenResult.refreshToken(),
                isNewMember,
                member.getAccount().getNickname(),
                member.isMigrationDecided()
        );
    }
}
