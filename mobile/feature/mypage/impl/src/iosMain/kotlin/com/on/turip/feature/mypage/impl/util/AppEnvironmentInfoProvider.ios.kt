package com.on.turip.feature.mypage.impl.util

import com.on.turip.feature.mypage.impl.model.AppEnvironmentInfoModel
import platform.UIKit.UIDevice

actual object AppEnvironmentInfoProvider {
    actual fun getAppEnvironmentInfo(): AppEnvironmentInfoModel =
        AppEnvironmentInfoModel(
            appVersionName = "unknown",
            appVersionCode = 0,
            deviceReleaseVersion = UIDevice.currentDevice.systemVersion,
            deviceSdkVersion = 0,
            deviceManufacturer = "Apple",
            deviceModel = UIDevice.currentDevice.model,
        )
}
