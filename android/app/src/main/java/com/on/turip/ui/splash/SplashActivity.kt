package com.on.turip.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.on.turip.ui.common.extensions.collectOnStarted
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.splash.SplashScreen
import com.on.turip.ui.compose.splash.SplashUiEffect
import com.on.turip.ui.compose.splash.SplashViewModel
import com.on.turip.ui.login.LoginActivity
import com.on.turip.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {
    private val viewModel: SplashViewModel by viewModels()
    private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }

    private val updateLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                Timber.d("업데이트 실패 ${result.resultCode}")
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TuripTheme {
                SplashScreen()
            }
        }

        checkUpdateAndProceed()
        observeUiEffect()
    }

    override fun onResume() {
        super.onResume()

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.canResumeUpdate(UPDATE_TYPE)) {
                appUpdateInfo.startUpdate(UPDATE_TYPE)
            }
        }
    }

    private fun checkUpdateAndProceed() {
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                when (appUpdateInfo.updateAvailability()) {
                    UpdateAvailability.UPDATE_AVAILABLE -> {
                        if (appUpdateInfo.isUpdateTypeAllowed(UPDATE_TYPE)) {
                            appUpdateInfo.startUpdate(UPDATE_TYPE)
                        } else {
                            proceed()
                        }
                    }

                    UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                        if (appUpdateInfo.isUpdateTypeAllowed(UPDATE_TYPE)) {
                            appUpdateInfo.startUpdate(UPDATE_TYPE)
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

    private fun observeUiEffect() {
        collectOnStarted(viewModel.uiEffect) { uiEffect ->
            when (uiEffect) {
                SplashUiEffect.NavigateToLogin -> {
                    startActivity(LoginActivity.newIntent(this@SplashActivity))
                    finish()
                }

                SplashUiEffect.NavigateToMain -> {
                    startActivity(MainActivity.newIntent(this@SplashActivity))
                    finish()
                }
            }
        }
    }

    private fun AppUpdateInfo.canResumeUpdate(updateType: Int): Boolean =
        updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS &&
            isUpdateTypeAllowed(updateType)

    private fun AppUpdateInfo.startUpdate(updateType: Int) {
        appUpdateManager.startUpdateFlowForResult(
            this,
            updateLauncher,
            AppUpdateOptions.newBuilder(updateType).build(),
        )
    }

    private fun proceed() {
        viewModel.determineStartDestination()
    }

    companion object {
        private const val UPDATE_TYPE = AppUpdateType.IMMEDIATE
    }
}
