package turip.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import turip.auth.controller.dto.request.TuripLoginRequest;
import turip.auth.controller.dto.response.TuripLoginResponse;
import turip.auth.service.dto.TuripLoginResult;
import turip.auth.util.TokenCookieUtil;
import turip.service.AdminAuthService;

@RestController
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;
    private final TokenCookieUtil tokenCookieUtil;

    @PostMapping("/api/v1/auth/login/admin")
    public ResponseEntity<TuripLoginResponse> login(
            @RequestHeader("device-fid") String deviceFid,
            @RequestBody TuripLoginRequest request) {
        TuripLoginResult result = adminAuthService.login(request, deviceFid);

        ResponseCookie accessTokenCookie = tokenCookieUtil.createAccessTokenCookie(result.tokenResult().accessToken());
        ResponseCookie refreshTokenCookie = tokenCookieUtil.createRefreshTokenCookie(
                result.tokenResult().refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(new TuripLoginResponse(result.nickname()));
    }

}
