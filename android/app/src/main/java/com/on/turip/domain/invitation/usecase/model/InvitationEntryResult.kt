package com.on.turip.domain.invitation.usecase.model

import com.on.turip.core.result.ErrorType
import com.on.turip.domain.turip.TuripInvitationInformation

/**
 * DetermineInvitationEntryRouteUseCase 에 대한 판단 결과.
 */
sealed interface InvitationEntryResult {
    /**
     * 회원 세션에서 초대 토큰 검증이 완료된 상태.
     *
     * [turipName]은 부가 정보이며 조회 실패 시 null일 수 있다.
     */
    data class MemberValidated(
        val invitationInformation: TuripInvitationInformation,
        val turipName: String?,
    ) : InvitationEntryResult

    /** 게스트/미초기화 세션으로 인증이 필요한 상태. */
    data object RequiresAuth : InvitationEntryResult

    /**
     * 유효하지 않은 초대 토큰으로 확정된 상태.
     *
     * 현재 서버에서 내려오는 에러 코드가 없어서 추후에 검증에 실패한 코드를 처리한다.
     * (현재는 불확실한 실패를 [Failure]로 처리)
     */
    data object InvalidInvitationToken : InvitationEntryResult

    /** 토큰 검증 중 발생한 시스템 실패(네트워크/서버/기타). */
    data class Failure(
        val errorType: ErrorType,
    ) : InvitationEntryResult
}
