package com.on.turip.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.on.turip.common.AuthState
import com.on.turip.common.UserType
import com.on.turip.databinding.ActivitySplashBinding
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private val viewModel: SplashViewmodel by viewModels()
    override val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }
    private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult(),
        ) { result ->
            if (result.resultCode != RESULT_OK) {
                Timber.d("업데이트 실패 ${result.resultCode}")
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.checkAutoLogin()
            checkUpdateAndProceed()
        }
    }

    override fun onResume() {
        super.onResume()

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            when {
                appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS &&
                    appUpdateInfo.isUpdateTypeAllowed(UPDATE_TYPE) -> {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activityResultLauncher,
                        AppUpdateOptions.newBuilder(UPDATE_TYPE).build(),
                    )
                }
            }
        }
    }

    private fun checkUpdateAndProceed() {
        val appUpdateInfoTask: Task<AppUpdateInfo> = appUpdateManager.appUpdateInfo

        appUpdateInfoTask
            .addOnSuccessListener { appUpdateInfo ->
                when (appUpdateInfo.updateAvailability()) {
                    UpdateAvailability.UPDATE_AVAILABLE -> {
                        if (appUpdateInfo.isUpdateTypeAllowed(UPDATE_TYPE)) {
                            appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                activityResultLauncher,
                                AppUpdateOptions.newBuilder(UPDATE_TYPE).build(),
                            )
                        } else {
                            navigateToLogin()
                        }
                    }

                    UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                        if (appUpdateInfo.isUpdateTypeAllowed(UPDATE_TYPE)) {
                            appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                activityResultLauncher,
                                AppUpdateOptions.newBuilder(UPDATE_TYPE).build(),
                            )
                        }
                    }

                    else -> {
                        navigateToLogin()
                    }
                }
            }.addOnFailureListener {
                navigateToLogin()
            }
    }

    private fun navigateToLogin() {
        when (AuthState.type) {
            UserType.MEMBER -> {
                startActivity(MainActivity.newIntent(this@SplashActivity))
                finish()
            }

            UserType.GUEST, UserType.NONE -> {
                startActivity(MainActivity.newIntent(this@SplashActivity))
                finish()
            }
        }
    }

    companion object {
        private const val UPDATE_TYPE = AppUpdateType.IMMEDIATE
    }
}
