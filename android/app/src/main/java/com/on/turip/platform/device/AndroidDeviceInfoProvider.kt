package com.on.turip.platform.device

import android.os.Build
import com.on.turip.BuildConfig
import com.on.turip.domain.common.DeviceInfo
import javax.inject.Inject

class AndroidDeviceInfoProvider @Inject constructor() : DeviceInfoProvider {
    override fun getDeviceInfo(): DeviceInfo =
        DeviceInfo(
            appVersionName = BuildConfig.VERSION_NAME,
            appVersionCode = BuildConfig.VERSION_CODE,
            deviceReleaseVersion = Build.VERSION.RELEASE,
            deviceSdkVersion = Build.VERSION.SDK_INT,
            deviceManufacturer = Build.MANUFACTURER,
            deviceModel = Build.MODEL,
        )
}
