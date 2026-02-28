package com.on.turip.data.accounts.datasource

import com.on.turip.core.result.TuripResult
import com.on.turip.data.accounts.dto.MyProfileResponse

interface AccountRemoteDataSource {
    suspend fun getMyProfile(): TuripResult<MyProfileResponse>
}
