package com.on.turip.core.network.datasource

import com.on.turip.core.network.dto.account.MyProfileResponse
import com.on.turip.core.network.service.MemberService

class DefaultAccountDatasource(
    private val memberService: MemberService,
) : AccountDatasource {
    override suspend fun getMyProfile(): MyProfileResponse =
        memberService.getMember()

    override suspend fun deleteAccount() =
        memberService.deleteMember()
}
