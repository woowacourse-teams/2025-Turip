package com.on.turip.feature.turipdetail.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.turipdetail.impl.model.turip.PlaceLatLngUiModel
import kotlinx.collections.immutable.ImmutableList

@Composable
internal actual fun PlatformTuripMap(
    selectedTuripId: Long,
    selectedPlace: PlaceLatLngUiModel,
    places: ImmutableList<PlaceLatLngUiModel>,
    modifier: Modifier,
) {
    Box(
        modifier = modifier.background(TuripTheme.colors.gray01),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = selectedPlace.name.ifBlank { places.firstOrNull()?.name.orEmpty() },
            style = TuripTheme.typography.title2,
            color = TuripTheme.colors.gray04,
            textAlign = TextAlign.Center,
        )
    }
}
