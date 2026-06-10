package com.on.turip.feature.turipdetail.impl.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.turipdetail.impl.model.turip.PlaceLatLngUiModel
import kotlinx.collections.immutable.ImmutableList

@Composable
fun TuripMapContent(
    selectedTuripId: Long,
    places: ImmutableList<PlaceLatLngUiModel>,
    selectedPlace: PlaceLatLngUiModel,
    isMapVisible: Boolean,
    onMapToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val mapHeight by animateDpAsState(
        targetValue = if (isMapVisible) 260.dp else 0.dp,
        animationSpec = tween(250),
    )

    Column(
        modifier = modifier.clipToBounds(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(mapHeight)
                    .background(TuripTheme.colors.gray01),
            contentAlignment = Alignment.Center,
        ) {
            if (isMapVisible) {
                Text(
                    text = selectedPlace.name.ifBlank { places.firstOrNull()?.name.orEmpty() },
                    style = TuripTheme.typography.title2,
                    color = TuripTheme.colors.gray04,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = onMapToggle,
                modifier = Modifier.align(Alignment.Center),
            ) {
                Icon(
                    imageVector = if (isMapVisible) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                )
            }
        }
    }
}
