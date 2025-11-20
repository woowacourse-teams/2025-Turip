package turip.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.auth.GoogleTokenParser;
import turip.auth.JwtProvider;
import turip.auth.controller.dto.request.LoginRequest;
import turip.auth.controller.dto.response.LoginResponse;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.UnauthorizedException;
import turip.member.domain.Member;
import turip.member.domain.Provider;
import turip.member.service.MemberService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final GoogleTokenParser googleTokenParser;
    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    public LoginResponse login(LoginRequest request, Provider provider) {
        if (provider.equals(Provider.GOOGLE)) {
            return loginWithGoogle(request);
        }
        throw new UnauthorizedException(ErrorTag.UNAUTHORIZED);
    }

    private LoginResponse loginWithGoogle(LoginRequest request) {
        String idToken = request.idToken();
        Provider provider = googleTokenParser.getProvider();
        String providerId = googleTokenParser.getProviderId(idToken);
        String email = googleTokenParser.getEmail(idToken);

        return createLoginResponse(provider, providerId, email);
    }

    private LoginResponse createLoginResponse(Provider provider, String providerId, String email) {
        boolean isNewMember = memberService.isFirstLogin(provider, providerId);
        Member member = memberService.findOrCreate(provider, providerId, email);

        String accessToken = jwtProvider.generateAccessToken(member.getAccount().getId());
        String refreshToken = jwtProvider.generateRefreshToken(member.getAccount().getId());

        return LoginResponse.of(accessToken, refreshToken, isNewMember);
    }
}
