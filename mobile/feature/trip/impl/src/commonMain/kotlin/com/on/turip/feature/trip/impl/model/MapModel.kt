package com.on.turip.feature.trip.impl.model

import androidx.compose.runtime.Stable
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.*
import com.on.turip.core.model.trip.MapType
import org.jetbrains.compose.resources.DrawableResource

@Stable
data class MapModel(
    val type: MapType,
    val uri: String,
) {
    val drawableRes: DrawableResource =
        when (type) {
            MapType.KAKAO -> Res.drawable.btn_kakao_map_basic
            MapType.GOOGLE -> Res.drawable.btn_google_map_basic
            MapType.NONE -> Res.drawable.btn_map_link
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
                uri = url,
            )
    }
}
