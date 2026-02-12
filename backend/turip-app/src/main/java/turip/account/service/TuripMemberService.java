package turip.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.controller.dto.request.TuripMemberRequest;
import turip.account.controller.dto.response.TuripMemberResponse;
import turip.account.domain.Member;
import turip.account.domain.TuripMember;
import turip.account.repository.TuripMemberRepository;
import turip.auth.controller.dto.request.TuripLoginRequest;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.common.exception.custom.ConflictException;
import turip.common.exception.custom.IllegalArgumentException;
import turip.common.exception.custom.NotFoundException;
import turip.common.exception.custom.UnauthorizedException;

@Service
@RequiredArgsConstructor
public class TuripMemberService {

    private final TuripMemberRepository turipMemberRepository;
    private final MemberService memberService;

    @Transactional
    public TuripMemberResponse create(TuripMemberRequest request) {
        validateDuplicatedLoginId(request.loginId());
        Member member = memberService.create(request.email());

        try {
            TuripMember turipMember = new TuripMember(member, request.loginId(), request.loginPassword());
            TuripMember savedTuripMember = turipMemberRepository.save(turipMember);
            return TuripMemberResponse.from(savedTuripMember);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getErrorTag());
        }
    }

    public TuripMember login(TuripLoginRequest request) {
        TuripMember turipMember = turipMemberRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new UnauthorizedException(ErrorTag.CREDENTIALS_INVALID));

        boolean isPasswordMatch = turipMember.isPasswordMatch(request.loginPassword());
        if (!isPasswordMatch) {
            throw new UnauthorizedException(ErrorTag.CREDENTIALS_INVALID);
        }

        return turipMember;
    }

    public TuripMember getByAccountId(Long accountId) {
        return turipMemberRepository.findByMemberAccountId(accountId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.MEMBER_NOT_FOUND));
    }

    private void validateDuplicatedLoginId(String loginId) {
        if (turipMemberRepository.existsByLoginId(loginId)) {
            throw new ConflictException(ErrorTag.LOGIN_ID_CONFLICT);
        }
    }
}
