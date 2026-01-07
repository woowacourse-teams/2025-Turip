package com.on.turip.data.login.datasource

import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.on.turip.core.result.TuripResult

interface CredentialProvider {
    suspend fun getIdToken(): TuripResult<GoogleIdTokenCredential>
}
