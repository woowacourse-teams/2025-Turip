package com.on.turip.ui.compose.trip.model

import android.net.Uri
import androidx.compose.runtime.Stable
import androidx.core.net.toUri
import com.on.turip.domain.trip.MapType

@Stable
data class MapModel(
    val type: MapType,
    val uri: Uri,
) {
    companion object {
        fun from(url: String): MapModel =
            MapModel(
                type = MapType.from(url),
                uri = url.toUri(),
            )
    }
}
