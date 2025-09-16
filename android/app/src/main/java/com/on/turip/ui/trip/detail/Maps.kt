package com.on.turip.ui.trip.detail

import android.net.Uri
import androidx.annotation.DrawableRes
import com.on.turip.R
import com.on.turip.ui.trip.detail.Maps.entries

private const val KAKAO_BASE_URL = "kakao.com"
private const val GOOGLE_BASE_URL = "google.com"

enum class Maps(
    val url: String?,
    @DrawableRes val iconRes: Int,
) {
    KAKAO(KAKAO_BASE_URL, R.drawable.btn_kakao_map_basic),
    GOOGLE(GOOGLE_BASE_URL, R.drawable.btn_google_map_basic),
    DEFAULT(null, R.drawable.btn_map_link),
    ;

    companion object {
        fun from(mapLink: Uri): Maps =
            entries.find { mapLink.toString().contains(it.url.orEmpty()) }
                ?: DEFAULT
    }
}
