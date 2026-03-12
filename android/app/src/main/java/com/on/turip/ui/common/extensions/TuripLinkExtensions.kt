package com.on.turip.ui.common.extensions

import com.on.turip.BuildConfig
import com.on.turip.domain.turip.TuripInvitationToken

fun TuripInvitationToken.toUrl(): String = "https://${BuildConfig.APP_LINK_TURIP_INVITATION_HOST}?token=$value"
