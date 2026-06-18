package com.on.turip.core.data.datasource

import com.on.turip.core.data.dto.account.MyProfileResponse
import com.on.turip.core.model.result.TuripResult

interface AccountRemoteDataSource {
    suspend fun getMyProfile(): TuripResult<MyProfileResponse>
}
