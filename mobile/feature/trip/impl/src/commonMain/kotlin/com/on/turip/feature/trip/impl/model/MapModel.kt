package com.on.turip.feature.trip.impl.model

import androidx.compose.runtime.Stable
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.btn_google_map_basic
import com.on.turip.core.designsystem.generated.resources.btn_kakao_map_basic
import com.on.turip.core.designsystem.generated.resources.btn_map_link
import com.on.turip.core.model.trip.MapType
import com.on.turip.core.ui.model.AppleMap
import com.on.turip.core.ui.util.usesUnifiedMapIcon
import org.jetbrains.compose.resources.DrawableResource

@Stable
data class MapModel(
    val type: MapType,
    val uri: String,
    val appleMap: AppleMap,
) {
    val drawableRes: DrawableResource =
        if (usesUnifiedMapIcon) {
            Res.drawable.btn_map_link
        } else {
            when (type) {
                MapType.KAKAO -> Res.drawable.btn_kakao_map_basic
                MapType.GOOGLE -> Res.drawable.btn_google_map_basic
                MapType.NONE -> Res.drawable.btn_map_link
            }
        }
    val enableTint: Boolean =
        if (usesUnifiedMapIcon) {
            true
        } else {
            when (type) {
                MapType.KAKAO -> false
                MapType.GOOGLE -> false
                MapType.NONE -> true
            }
        }

    companion object {
        fun from(
            url: String,
            appleMap: AppleMap,
        ): MapModel =
            MapModel(
                type = MapType.from(url),
                uri = url,
                appleMap = appleMap,
            )
    }
}
