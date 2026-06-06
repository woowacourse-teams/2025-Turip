package com.on.turip.core.data.datasource

import com.on.turip.core.model.result.TuripResult
import com.on.turip.core.data.dto.account.MyProfileResponse

interface AccountRemoteDataSource {
    suspend fun getMyProfile(): TuripResult<MyProfileResponse>
}
