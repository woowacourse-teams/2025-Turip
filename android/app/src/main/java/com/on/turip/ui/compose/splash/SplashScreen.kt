package com.on.turip.ui.compose.splash

import android.app.Activity.RESULT_OK
import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

private const val UPDATE_TYPE = AppUpdateType.IMMEDIATE

@Composable
fun SplashScreen(
    deepLinkUrl: String?,
    onNavigateToMain: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToInvitationEntry: (deepLinkUrl: String) -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val localContext: Context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val appUpdateManager: AppUpdateManager =
        remember(localContext) { AppUpdateManagerFactory.create(localContext) }

    val updateLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult> =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
        ) { result ->
            if (result.resultCode != RESULT_OK) {
                Timber.d("업데이트 실패/취소 ${result.resultCode}")
                onFinish()
            } else {
                viewModel.determineStartDestination(deepLinkUrl)
            }
        }

    LaunchedEffect(Unit) {
        checkUpdateAndProceed(
            appUpdateManager = appUpdateManager,
            updateLauncher = updateLauncher,
            proceed = { viewModel.determineStartDestination(deepLinkUrl) },
        )
    }
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collectLatest { uiEffect ->
            when (uiEffect) {
                SplashUiEffect.NavigateToMain -> {
                    onNavigateToMain()
                }

                SplashUiEffect.NavigateToLogin -> {
                    onNavigateToLogin()
                }

                is SplashUiEffect.NavigateToInvitationEntry -> {
                    onNavigateToInvitationEntry(uiEffect.deepLinkUrl)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.canResumeUpdate(updateType = UPDATE_TYPE)) {
                    appUpdateInfo.startUpdate(
                        appUpdateManager = appUpdateManager,
                        updateLauncher = updateLauncher,
                        updateType = UPDATE_TYPE,
                    )
                }
            }
        }
    }

    SplashScreenContent(modifier)
}

@Composable
private fun SplashScreenContent(modifier: Modifier = Modifier) {
    // windowSplashScreenBackground 속성과 통일하기 위해 colors 에 정의된 값을 사용
    val splashBackgroundColor = colorResource(R.color.splash_background)

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(color = splashBackgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_turip_logo_kr),
                contentDescription = null,
                modifier = Modifier.size(120.dp),
            )

            Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

            Text(
                text = stringResource(id = R.string.all_turip_description),
                style = TuripTheme.typography.title3,
                color = TuripTheme.colors.white,
            )
        }
    }
}

private fun checkUpdateAndProceed(
    appUpdateManager: AppUpdateManager,
    updateLauncher: ActivityResultLauncher<IntentSenderRequest>,
    proceed: () -> Unit,
) {
    appUpdateManager.appUpdateInfo
        .addOnSuccessListener { appUpdateInfo ->
            when (appUpdateInfo.updateAvailability()) {
                UpdateAvailability.UPDATE_AVAILABLE -> {
                    if (appUpdateInfo.isUpdateTypeAllowed(UPDATE_TYPE)) {
                        appUpdateInfo.startUpdate(
                            appUpdateManager,
                            updateLauncher,
                            UPDATE_TYPE,
                        )
                    } else {
                        proceed()
                    }
                }

                UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                    if (appUpdateInfo.isUpdateTypeAllowed(UPDATE_TYPE)) {
                        appUpdateInfo.startUpdate(
                            appUpdateManager,
                            updateLauncher,
                            UPDATE_TYPE,
                        )
                    } else {
                        proceed()
                    }
                }

                else -> {
                    proceed()
                }
            }
        }.addOnFailureListener {
            proceed()
        }
}

private fun AppUpdateInfo.canResumeUpdate(updateType: Int): Boolean =
    updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS &&
        isUpdateTypeAllowed(updateType)

private fun AppUpdateInfo.startUpdate(
    appUpdateManager: AppUpdateManager,
    updateLauncher: ActivityResultLauncher<IntentSenderRequest>,
    updateType: Int,
) {
    appUpdateManager.startUpdateFlowForResult(
        this,
        updateLauncher,
        AppUpdateOptions.newBuilder(updateType).build(),
    )
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    TuripTheme {
        SplashScreenContent()
    }
}
