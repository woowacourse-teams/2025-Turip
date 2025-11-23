package com.on.turip.data.login.repository

import com.on.turip.data.login.datasource.ThirdPartyLoginRemoteDatasource
import com.on.turip.domain.login.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class DefaultLoginRepository @Inject constructor(
    val thirdPartyLoginRemoteDatasource: ThirdPartyLoginRemoteDatasource,
) : LoginRepository {
    override fun login() {
        CoroutineScope(Dispatchers.IO).launch {
            val a =
                async {
                    thirdPartyLoginRemoteDatasource.getIdToken()
                }.await()
        }
    }
}
