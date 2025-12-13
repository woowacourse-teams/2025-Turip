package com.on.turip.domain.setting

data class InquiryMail(
    val appVersionName: String,
    val appVersionCode: Int,
    val deviceReleaseVersion: String,
    val deviceSdkVersion: Int,
    val deviceManufacturer: String,
    val deviceModel: String,
    val fid: String,
) {
    val content: String
        get() =
            """
            문의 사항을 편하게 작성해주세요! 🙂
            
            문의 내용 작성:
            
            
            
            
            
            
            
            
            
            
            
            
            --------------------------------------------------------
            
            사용자의 튜립 앱 버전: $appVersionName ($appVersionCode)
            사용자의 OS: Android $deviceReleaseVersion (SDK $deviceSdkVersion)
            사용자 기기: $deviceManufacturer $deviceModel
            사용자 ID: $fid
            """.trimIndent()

    companion object {
        val RECIPIENT: String = "team.turip@gmail.com"
        val TITLE: String = "튜립 사용 문의 및 불편 사항 건의"
    }
}
