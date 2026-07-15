package com.on.turip.feature.main.component

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.content.ContextCompat
import com.on.turip.core.data.session.SessionManager
import com.on.turip.core.domain.session.SessionState
import com.on.turip.core.domain.usecase.RegisterFcmTokenUseCase
import com.on.turip.core.model.result.onFailureWithCause
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
actual fun NotificationPermissionEffect() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val activity = LocalActivity.current ?: return
    val sessionManager: SessionManager = koinInject()
    val registerFcmTokenUseCase: RegisterFcmTokenUseCase = koinInject()
    val coroutineScope = rememberCoroutineScope()

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (isGranted && sessionManager.state.value == SessionState.Member) {
                    coroutineScope.launch {
                        registerFcmTokenUseCase().onFailureWithCause { errorType, cause ->
                            Napier.w("FCM 토큰 등록 실패. errorType=$errorType", cause, tag = "FcmToken")
                        }
                    }
                }
            },
        )

    LaunchedEffect(Unit) {
        val isGranted =
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        if (!isGranted) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
