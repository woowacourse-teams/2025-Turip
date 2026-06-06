package com.on.turip.core.network.datasource

import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.network.dto.account.MyProfileResponse

interface AccountRemoteDataSource {
    suspend fun getMyProfile(): TuripResult<MyProfileResponse>
}
