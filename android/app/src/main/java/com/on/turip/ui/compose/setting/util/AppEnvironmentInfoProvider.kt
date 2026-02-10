package com.on.turip.ui.compose.setting.util

import android.os.Build
import com.on.turip.BuildConfig
import com.on.turip.ui.compose.setting.model.AppEnvironmentInfoModel

object AppEnvironmentInfoProvider {
    fun getAppEnvironmentInfo(): AppEnvironmentInfoModel =
        AppEnvironmentInfoModel(
            appVersionName = BuildConfig.VERSION_NAME,
            appVersionCode = BuildConfig.VERSION_CODE,
            deviceReleaseVersion = Build.VERSION.RELEASE,
            deviceSdkVersion = Build.VERSION.SDK_INT,
            deviceManufacturer = Build.MANUFACTURER,
            deviceModel = Build.MODEL,
        )
}
