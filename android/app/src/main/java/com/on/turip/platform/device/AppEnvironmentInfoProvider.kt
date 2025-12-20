package com.on.turip.platform.device

import com.on.turip.domain.common.AppEnvironmentInfo

interface AppEnvironmentInfoProvider {
    fun getAppEnvironmentInfo(): AppEnvironmentInfo
}
