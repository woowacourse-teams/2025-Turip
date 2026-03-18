package com.on.turip.data.login.datasource

import com.on.turip.core.result.TuripResult

interface CredentialProvider {
    suspend fun getIdToken(): TuripResult<String>
}
