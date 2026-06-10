package com.on.turip.feature.turipdetail.impl.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.*
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.turipdetail.impl.model.MapModel
import com.on.turip.feature.turipdetail.impl.model.TuripPlaceModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TuripPlaces(
    places: ImmutableList<TuripPlaceModel>,
    onItemClick: (placeId: Long) -> Unit,
    onMapClick: (map: MapModel) -> Unit,
    onTuripPlaceClick: (placeId: Long) -> Unit,
    onDragStart: () -> Unit,
    onDragPlace: (from: Int, to: Int) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(color = TuripTheme.colors.white),
    ) {
        if (places.isEmpty()) {
            EmptyTuripPlaces(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .weight(1f),
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
                contentPadding = PaddingValues(TuripTheme.spacing.large),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(items = places, key = { it.turipPlaceId }) { place ->
                    TuripPlaceRow(
                        place = place,
                        onItemClick = { onItemClick(place.placeId) },
                        onMapClick = { onMapClick(place.mapModel) },
                        onTuripPlaceClick = { onTuripPlaceClick(place.placeId) },
                    )
                }
            }
        }
    }
}

@Composable
private fun TuripPlaceRow(
    place: TuripPlaceModel,
    onItemClick: () -> Unit,
    onMapClick: () -> Unit,
    onTuripPlaceClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(TuripTheme.colors.container, TuripTheme.shape.container)
                .clickable(onClick = onItemClick)
                .padding(TuripTheme.spacing.large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = place.category,
                style = TuripTheme.typography.info1,
                color = TuripTheme.colors.gray05,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(TuripTheme.spacing.extraSmall))
            Text(
                text = place.name,
                style = TuripTheme.typography.title2,
                color = TuripTheme.colors.gray04,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        IconButton(onClick = onMapClick) {
            Icon(
                imageVector = Icons.Default.Map,
                contentDescription = null,
                tint = TuripTheme.colors.gray04,
            )
        }
        IconButton(onClick = onTuripPlaceClick) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = TuripTheme.colors.error,
            )
        }
    }
}

@Composable
fun EmptyTuripPlaces(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(Res.drawable.mascot),
                contentDescription = stringResource(Res.string.all_mascot_description),
            )

            Spacer(modifier = Modifier.height(TuripTheme.spacing.extraLarge))

            Text(
                text = stringResource(Res.string.turip_place_empty_suggest_title),
                style = TuripTheme.typography.title1,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(TuripTheme.spacing.small))

            Text(
                text = stringResource(Res.string.turip_place_empty_suggest_description),
                style = TuripTheme.typography.title2,
                color = TuripTheme.colors.gray03,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TuripPlacesPreview() {
    TuripTheme {
        TuripPlaces(
            places =
                persistentListOf(
                    TuripPlaceModel.Idle.copy(
                        turipPlaceId = 1L,
                        placeId = 1L,
                        name = "안국역",
                        category = "역",
                    ),
                ),
            onMapClick = {},
            onTuripPlaceClick = {},
            onDragStart = {},
            onDragPlace = { _, _ -> },
            onDragEnd = {},
            onItemClick = {},
        )
    }
}
