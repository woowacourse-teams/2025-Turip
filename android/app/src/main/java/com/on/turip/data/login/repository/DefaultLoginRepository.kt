package com.on.turip.data.login.repository

import com.on.turip.data.common.onSuccess
import com.on.turip.data.login.datasource.LoginDatasource
import com.on.turip.domain.login.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class DefaultLoginRepository @Inject constructor(
    val loginDatasource: LoginDatasource,
) : LoginRepository {
    override fun login(idToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            loginDatasource.postIdToken(idToken).onSuccess { result ->
                Timber.d("토큰들 $result")
            }
        }
    }
}
