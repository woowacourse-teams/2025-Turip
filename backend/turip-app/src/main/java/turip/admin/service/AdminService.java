package turip.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.Member;
import turip.auth.controller.dto.request.TuripLoginRequest;
import turip.auth.controller.dto.response.TokenResult;
import turip.auth.service.AuthService;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.ForbiddenException;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AuthService authService;

    @Transactional
    public TokenResult login(TuripLoginRequest request, String deviceFid) {
        Member member = authService.loginAndGetMember(request);
        if (!member.getAccount().isAdmin()) {
            throw new ForbiddenException(ErrorTag.FORBIDDEN);
        }

        return authService.processTuripLogin(deviceFid, member);
    }

}
