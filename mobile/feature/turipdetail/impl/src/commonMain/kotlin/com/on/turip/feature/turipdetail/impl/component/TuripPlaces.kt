package com.on.turip.feature.turipdetail.impl.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.core.designsystem.generated.resources.*
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.turipdetail.impl.model.MapModel
import com.on.turip.feature.turipdetail.impl.model.TuripPlaceModel
import com.on.turip.feature.turipdetail.impl.util.reorderable.ReorderableItem
import com.on.turip.feature.turipdetail.impl.util.reorderable.rememberReorderableLazyColumnState
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
    val hasPlaces = places.isNotEmpty()
    val listState = rememberLazyListState()

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(color = TuripTheme.colors.white),
    ) {
        if (hasPlaces) {
            TuripPlaces(
                listState = listState,
                places = places,
                onMapClick = onMapClick,
                onTuripPlaceClick = onTuripPlaceClick,
                onDragStart = onDragStart,
                onDragPlace = onDragPlace,
                onItemClick = onItemClick,
                onDragEnd = onDragEnd,
                modifier = Modifier.padding(horizontal = TuripTheme.spacing.large),
            )
        } else {
            EmptyTuripPlaces(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .weight(1f),
            )
        }
    }
}

@Composable
private fun TuripPlaces(
    listState: LazyListState,
    places: ImmutableList<TuripPlaceModel>,
    onItemClick: (placeId: Long) -> Unit,
    onMapClick: (map: MapModel) -> Unit,
    onTuripPlaceClick: (placeId: Long) -> Unit,
    onDragStart: () -> Unit,
    onDragPlace: (from: Int, to: Int) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val reorderableItemShape = TuripTheme.shape.container
    val reorderableLazyColumnState =
        rememberReorderableLazyColumnState(
            lazyListState = listState,
            onDragStart = onDragStart,
            onDragEnd = onDragEnd,
            onMove = { from, to ->
                onDragPlace(from.index, to.index)
            },
        )

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
        contentPadding = PaddingValues(vertical = TuripTheme.spacing.medium),
        modifier = modifier.fillMaxHeight(),
    ) {
        items(items = places, key = { it.turipPlaceId }) { place ->
            ReorderableItem(
                state = reorderableLazyColumnState,
                key = place.turipPlaceId,
            ) { isDragging: Boolean ->
                val interactionSource = remember { MutableInteractionSource() }
                val elevation by animateFloatAsState(if (isDragging) 8.0f else 0.0f)
                TuripPlaceItem(
                    place = place,
                    onMapClick = { onMapClick(place.mapModel) },
                    onTuripPlaceClick = { onTuripPlaceClick(place.placeId) },
                    onItemClick = { onItemClick(place.placeId) },
                    modifier =
                        Modifier
                            .graphicsLayer {
                                shadowElevation = elevation
                                shape = reorderableItemShape
                                clip = true
                            }.draggableAfterLongPress(
                                interactionSource = interactionSource,
                            ),
                )
            }
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
