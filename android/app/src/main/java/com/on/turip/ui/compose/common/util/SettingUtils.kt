package com.on.turip.ui.compose.common.util

import android.os.Build
import com.on.turip.BuildConfig

object SettingUtils {
    const val EMAIL_RECIPIENT: String = "team.turip@gmail.com"
    const val EMAIL_SUBJECT: String = "튜립 사용 문의 및 불편 사항 건의"
    private val EMAIL_BODY_FORMAT: String =
        """
        문의 사항을 편하게 작성해주세요! 🙂
        
        문의 내용 작성:
        
        
        
        
        
        
        
        
        
        
        
        
        --------------------------------------------------------
        
        사용자의 튜립 앱 버전: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})
        사용자의 OS: Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})
        사용자 기기: ${Build.MANUFACTURER} ${Build.MODEL}
        사용자 ID: %s
        """.trimIndent()
    const val PRIVACY_POLICY_LINK =
        "https://agate-bandana-491.notion.site/23aeabcebdc180299e11d3bb2fbfaf67?source=copy_link"

    fun toEmailBody(fid: String): String = EMAIL_BODY_FORMAT.format(fid)
}
