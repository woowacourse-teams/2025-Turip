package com.on.turip.domain.invitation.usecase

import com.on.turip.core.result.fold
import com.on.turip.domain.invitation.usecase.model.InvitationEntryResult
import com.on.turip.domain.session.SessionState
import com.on.turip.domain.session.SessionStore
import com.on.turip.domain.turip.TuripInvitationInformation
import com.on.turip.domain.turip.repository.TuripRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * 초대 링크 판단, 현재 세션 상태와 초대 토큰 유효성을 기준으로 도메인 판단 결과를 반환한다.
 *
 * 동작 요약:
 * - 초대토큰이 비어 있으면 [InvitationEntryResult.InvalidInvitationToken]을 반환한다.
 * - 토큰 검증에 성공하면
 *   - 멤버 세션은 [InvitationEntryResult.MemberValidated]를 반환한다.
 *   - 게스트/미초기화 세션은 [InvitationEntryResult.RequiresAuth]를 반환한다.
 * - 토큰 검증에 실패하면 [InvitationEntryResult.InvalidInvitationToken]을 반환한다.
 */
class DetermineInvitationEntryRouteUseCase @Inject constructor(
    private val turipRepository: TuripRepository,
    private val sessionStore: SessionStore,
) {
    suspend operator fun invoke(invitationToken: String?): InvitationEntryResult {
        val sessionState: SessionState = sessionStore.state.value
        val trimmedInvitationToken: String =
            invitationToken?.takeIf(String::isNotBlank)?.trim()
                ?: return InvitationEntryResult.InvalidInvitationToken

        return turipRepository
            .verifyInvitationToken(trimmedInvitationToken)
            .fold(
                onSuccess = { information: TuripInvitationInformation ->
                    when (sessionState) {
                        SessionState.Member -> {
                            val turipName: String? =
                                turipRepository.loadTurip(information.turipId).fold(
                                    onSuccess = { it.name },
                                    onFailure = {
                                        // 네트워크, 서버 에러의 경우 재요청하기엔 토큰 검증 후 이동이 주요 기능이여서 null 로 반환 후 기본값 제공
                                        Timber.e("초대 토큰에 대한 튜립 id를 가져오지 못했어요, turipId = ${information.turipId}")
                                        null
                                    },
                                )
                            InvitationEntryResult.MemberValidated(
                                invitationInformation = information,
                                turipName = turipName,
                            )
                        }

                        SessionState.Guest, SessionState.Uninitialized -> {
                            InvitationEntryResult.RequiresAuth
                        }
                    }
                },
                onFailure = { errorType ->
                    // TODO: 서버에서 "유효하지 않은 초대 토큰" 오류를 명시적으로 내려주기 시작하면 InvalidInvitationToken 으로 처리 필요
                    Timber.e("튜립 초대 토큰 검증 실패 invitationToken = $invitationToken")
                    InvitationEntryResult.Failure(errorType)
                },
            )
    }
}
