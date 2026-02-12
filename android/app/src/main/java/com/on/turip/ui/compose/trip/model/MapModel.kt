package com.on.turip.ui.compose.trip.model

import android.net.Uri
import androidx.compose.runtime.Stable
import androidx.core.net.toUri
import com.on.turip.R
import com.on.turip.domain.trip.MapType

@Stable
data class MapModel(
    val type: MapType,
    val uri: Uri,
) {
    val drawableRes: Int =
        when (type) {
            MapType.KAKAO -> R.drawable.btn_kakao_map_basic
            MapType.GOOGLE -> R.drawable.btn_google_map_basic
            MapType.NONE -> R.drawable.btn_map_link
        }
    val enableTint: Boolean =
        when (type) {
            MapType.KAKAO -> false
            MapType.GOOGLE -> false
            MapType.NONE -> true
        }

    companion object {
        fun from(url: String): MapModel =
            MapModel(
                type = MapType.from(url),
                uri = url.toUri(),
            )
    }
}
