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
import turip.auth.controller.dto.response.TokenResult;
import turip.auth.util.TokenCookieUtil;
import turip.service.AdminAuthService;

@RestController
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;
    private final TokenCookieUtil tokenCookieUtil;

    @PostMapping("/login/admin")
    public ResponseEntity<Void> login(
            @RequestHeader("device-fid") String deviceFid,
            @RequestBody TuripLoginRequest request) {
        TokenResult result = adminAuthService.login(request, deviceFid);

        ResponseCookie accessTokenCookie = tokenCookieUtil.createAccessTokenCookie(result.accessToken());
        ResponseCookie refreshTokenCookie = tokenCookieUtil.createRefreshTokenCookie(result.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }

}
