package com.on.turip.domain.common

data class DeviceInfo(
    val appVersionName: String,
    val appVersionCode: Int,
    val deviceReleaseVersion: String,
    val deviceSdkVersion: Int,
    val deviceManufacturer: String,
    val deviceModel: String,
)
