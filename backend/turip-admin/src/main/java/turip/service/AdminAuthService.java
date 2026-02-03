package turip.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.Member;
import turip.auth.controller.dto.request.TuripLoginRequest;
import turip.auth.service.AuthService;
import turip.auth.service.dto.TuripLoginResult;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.ForbiddenException;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AuthService authService;

    @Transactional
    public TuripLoginResult login(TuripLoginRequest request, String deviceFid) {
        Member member = authService.loginAndGetMember(request);
        if (!member.getAccount().isAdmin()) {
            throw new ForbiddenException(ErrorTag.FORBIDDEN);
        }

        return new TuripLoginResult(authService.processTuripLogin(deviceFid, member), member.getNickname());
    }

}
