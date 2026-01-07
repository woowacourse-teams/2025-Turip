package turip.account.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.account.controller.dto.request.TuripMemberRequest;
import turip.account.controller.dto.response.TuripMemberResponse;
import turip.account.domain.Member;
import turip.account.domain.TuripMember;
import turip.account.repository.TuripMemberRepository;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.common.exception.custom.ConflictException;
import turip.common.exception.custom.IllegalArgumentException;

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

    private void validateDuplicatedLoginId(String loginId) {
        if (turipMemberRepository.existsByLoginId(loginId)) {
            throw new ConflictException(ErrorTag.LOGIN_ID_CONFLICT);
        }
    }
}
