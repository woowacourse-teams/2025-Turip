package com.on.turip.core.domain.usecase

import com.on.turip.core.domain.repository.TuripRepository
import com.on.turip.core.domain.session.SessionState
import com.on.turip.core.model.invitation.InvitationEntryResult
import com.on.turip.core.model.result.fold
import com.on.turip.core.model.turip.TuripInvitationInformation
import io.github.aakira.napier.Napier

class DetermineInvitationEntryRouteUseCase(
    private val turipRepository: TuripRepository,
) {
    suspend operator fun invoke(
        sessionState: SessionState,
        invitationToken: String?,
    ): InvitationEntryResult {
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
                                        Napier.e("튜립 초대 토큰의 튜립 정보를 가져오지 못했어요, turipId=${information.turipId}")
                                        null
                                    },
                                )
                            InvitationEntryResult.MemberValidated(
                                invitationInformation = information,
                                turipName = turipName,
                            )
                        }

                        SessionState.Guest,
                        SessionState.Uninitialized,
                        -> InvitationEntryResult.RequiresAuth
                    }
                },
                onFailure = { errorType ->
                    Napier.e("튜립 초대 토큰 검증 실패 invitationToken=$invitationToken")
                    InvitationEntryResult.Failure(errorType)
                },
            )
    }
}
