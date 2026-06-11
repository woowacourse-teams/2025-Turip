package com.on.turip.feature.mypage.impl.model

data class InquiryMail(
    val appEnvironmentInfo: AppEnvironmentInfoModel,
    val fid: String,
) {
    val content: String
        get() =
            """
            문의 사항을 편하게 작성해주세요! 🙂
            
            문의 내용 작성:
            
            
            
            
            
            
            
            
            
            
            
            
            --------------------------------------------------------
            
            사용자의 튜립 앱 버전: ${appEnvironmentInfo.appVersionName} (${appEnvironmentInfo.appVersionCode})
            사용자의 OS: ${appEnvironmentInfo.osName} ${appEnvironmentInfo.deviceReleaseVersion}${appEnvironmentInfo.sdkDescription}
            사용자 기기: ${appEnvironmentInfo.deviceManufacturer} ${appEnvironmentInfo.deviceModel}
            사용자 ID: $fid
            """.trimIndent()

    companion object {
        const val RECIPIENT: String = "team.turip@gmail.com"
        const val TITLE: String = "튜립 사용 문의 및 불편 사항 건의"
    }
}

private val AppEnvironmentInfoModel.sdkDescription: String
    get() =
        if (deviceSdkVersion > 0) {
            " (SDK $deviceSdkVersion)"
        } else {
            ""
        }
