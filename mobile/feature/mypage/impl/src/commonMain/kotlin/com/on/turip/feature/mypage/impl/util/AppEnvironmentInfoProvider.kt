package com.on.turip.feature.mypage.impl.util

import com.on.turip.feature.mypage.impl.model.AppEnvironmentInfoModel

expect object AppEnvironmentInfoProvider {
    fun getAppEnvironmentInfo(): AppEnvironmentInfoModel
}
