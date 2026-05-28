package com.on.turip.feature.turipdetail.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.model.TuripPlace
import kotlinx.collections.immutable.ImmutableList

@Composable
fun TuripPlaces(
    places: ImmutableList<TuripPlace>,
    onRemovePlace: (placeId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (places.isEmpty()) {
        EmptyTuripPlaces(modifier = modifier)
    } else {
        LazyColumn(
            contentPadding = PaddingValues(vertical = TuripTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = TuripTheme.spacing.large),
        ) {
            items(items = places, key = { it.place.id }) { turipPlace ->
                PlaceItem(
                    turipPlace = turipPlace,
                    onRemove = { onRemovePlace(turipPlace.place.id) },
                )
            }
        }
    }
}

@Composable
private fun PlaceItem(
    turipPlace: TuripPlace,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, TuripTheme.colors.border, TuripTheme.shape.container)
            .clip(TuripTheme.shape.container)
            .background(TuripTheme.colors.white)
            .padding(TuripTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(TuripTheme.shape.container)
                .background(TuripTheme.colors.chipBackground),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = TuripTheme.colors.primary,
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = turipPlace.place.name,
                style = TuripTheme.typography.title3,
                color = TuripTheme.colors.black,
            )
            Text(
                text = turipPlace.place.category,
                style = TuripTheme.typography.info2,
                color = TuripTheme.colors.gray03,
            )
        }

        IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "장소 삭제",
                tint = TuripTheme.colors.gray03,
            )
        }
    }
}

@Composable
private fun EmptyTuripPlaces(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = TuripTheme.colors.gray02,
                modifier = Modifier.size(48.dp),
            )
            Text(
                text = "아직 저장한 장소가 없어요",
                style = TuripTheme.typography.title1,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = TuripTheme.spacing.large),
            )
            Text(
                text = "콘텐츠에서 장소를 저장해보세요",
                style = TuripTheme.typography.title2,
                color = TuripTheme.colors.gray03,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = TuripTheme.spacing.small),
            )
        }
    }
}
