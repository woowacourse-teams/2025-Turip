package com.on.turip.domain.setting

import com.on.turip.domain.common.DeviceInfo

data class InquiryMail(
    val deviceInfo: DeviceInfo,
    val fid: String,
) {
    val content: String
        get() =
            """
            문의 사항을 편하게 작성해주세요! 🙂
            
            문의 내용 작성:
            
            
            
            
            
            
            
            
            
            
            
            
            --------------------------------------------------------
            
            사용자의 튜립 앱 버전: ${deviceInfo.appVersionName} (${deviceInfo.appVersionCode})
            사용자의 OS: Android ${deviceInfo.deviceReleaseVersion} (SDK ${deviceInfo.deviceSdkVersion})
            사용자 기기: ${deviceInfo.deviceManufacturer} ${deviceInfo.deviceModel}
            사용자 ID: $fid
            """.trimIndent()

    companion object {
        const val RECIPIENT: String = "team.turip@gmail.com"
        const val TITLE: String = "튜립 사용 문의 및 불편 사항 건의"
    }
}
