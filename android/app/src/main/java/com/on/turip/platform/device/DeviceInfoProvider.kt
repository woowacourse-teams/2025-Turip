package com.on.turip.platform.device

import com.on.turip.domain.common.DeviceInfo

interface DeviceInfoProvider {
    fun getDeviceInfo(): DeviceInfo
}
