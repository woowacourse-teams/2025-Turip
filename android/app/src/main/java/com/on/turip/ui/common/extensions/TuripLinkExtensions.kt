package com.on.turip.ui.common.extensions

import com.on.turip.BuildConfig
import com.on.turip.domain.turip.TuripInvitationToken
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * 초대 토큰을 앱 링크 URL로 변환한다.
 * query string 손상을 막기 위해 토큰 값은 UTF-8로 URL 인코딩한다.
 */
fun TuripInvitationToken.toUrl(): String {
    val encodedToken = URLEncoder.encode(value, StandardCharsets.UTF_8.name())
    return "https://${BuildConfig.APP_LINK_TURIP_INVITATION_HOST}?token=$encodedToken"
}
