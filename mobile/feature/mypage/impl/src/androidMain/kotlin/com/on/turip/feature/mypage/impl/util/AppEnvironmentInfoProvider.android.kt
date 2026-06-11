package com.on.turip.feature.mypage.impl.util

import android.os.Build
import com.on.turip.feature.mypage.impl.BuildConfig
import com.on.turip.feature.mypage.impl.model.AppEnvironmentInfoModel

actual object AppEnvironmentInfoProvider {
    actual fun getAppEnvironmentInfo(): AppEnvironmentInfoModel =
        AppEnvironmentInfoModel(
            appVersionName = BuildConfig.VERSION_NAME,
            appVersionCode = BuildConfig.VERSION_CODE,
            deviceReleaseVersion = Build.VERSION.RELEASE,
            deviceSdkVersion = Build.VERSION.SDK_INT,
            deviceManufacturer = Build.MANUFACTURER,
            deviceModel = Build.MODEL,
        )
}
