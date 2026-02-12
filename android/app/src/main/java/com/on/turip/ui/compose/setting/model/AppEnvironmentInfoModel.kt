package com.on.turip.ui.compose.setting.model

data class AppEnvironmentInfoModel(
    val appVersionName: String,
    val appVersionCode: Int,
    val deviceReleaseVersion: String,
    val deviceSdkVersion: Int,
    val deviceManufacturer: String,
    val deviceModel: String,
)
