package com.on.turip.data.login.datasource

import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.on.turip.data.common.TuripCustomResult

interface CredentialProvider {
    suspend fun getIdToken(): TuripCustomResult<GoogleIdTokenCredential>
}
