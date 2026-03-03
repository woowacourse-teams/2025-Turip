package com.on.turip.data.account.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.account.dto.MyProfileResponse

interface AccountRemoteDataSource {
    suspend fun getMyProfile(): TuripResult<MyProfileResponse>
}
