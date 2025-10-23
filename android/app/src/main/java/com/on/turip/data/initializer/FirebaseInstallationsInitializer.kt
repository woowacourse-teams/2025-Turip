package com.on.turip.data.initializer

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.installations.FirebaseInstallations
import com.on.turip.domain.userstorage.TuripDeviceIdentifier
import com.on.turip.domain.userstorage.repository.UserStorageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class FirebaseInstallationsInitializer @Inject constructor(
    private val userStorageRepository: UserStorageRepository,
) {
    fun setupFirebaseInstallationId(coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)) {
        FirebaseInstallations.getInstance().id.addOnCompleteListener { task: Task<String> ->
            if (task.isSuccessful) {
                coroutineScope
                    .launch {
                        userStorageRepository.createId(TuripDeviceIdentifier(task.result))
                    }.also {
                        Log.d("moongchi", "setupFirebaseInstallationId: ${task.result}")
                    }
            } else {
                // TODO : Firebase Installation ID 가져오지 못했을 때
            }
        }
    }
}
