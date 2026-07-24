package com.on.turip.feature.trip.impl.turipselection.component

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_back_description
import com.on.turip.core.designsystem.generated.resources.all_mascot_description
import com.on.turip.core.designsystem.generated.resources.all_share_description
import com.on.turip.core.designsystem.generated.resources.all_total_place_count
import com.on.turip.core.designsystem.generated.resources.ic_share
import com.on.turip.core.designsystem.generated.resources.mascot
import com.on.turip.core.designsystem.generated.resources.turip_place_empty_suggest_description
import com.on.turip.core.designsystem.generated.resources.turip_place_empty_suggest_title
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.trip.impl.model.MapModel
import com.on.turip.feature.trip.impl.turipselection.model.TuripPlaceModel
import com.on.turip.feature.trip.impl.turipselection.util.reorderable.ReorderableItem
import com.on.turip.feature.trip.impl.turipselection.util.reorderable.rememberReorderableLazyColumnState
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TuripDetail(
    turipName: String,
    places: ImmutableList<TuripPlaceModel>,
    onMapClick: (map: MapModel) -> Unit,
    onTuripPlaceClick: (place: TuripPlaceModel) -> Unit,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
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
        Header(
            turipName = turipName,
            isVisibleShare = hasPlaces,
            onBackClick = onBackClick,
            onShareClick = onShareClick,
        )

        if (hasPlaces) {
            TuripPlacesContent(
                listState = listState,
                places = places,
                onMapClick = onMapClick,
                onTuripPlaceClick = onTuripPlaceClick,
                onDragStart = onDragStart,
                onDragPlace = onDragPlace,
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
private fun Header(
    turipName: String,
    isVisibleShare: Boolean,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
) {
    PlaceTuripSelectionBottomSheetHeader(
        title = turipName,
        navigation = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = stringResource(Res.string.all_back_description),
                    tint = TuripTheme.colors.gray03,
                )
            }
        },
        actions = {
            if (isVisibleShare) {
                IconButton(onClick = onShareClick) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_share),
                        contentDescription = stringResource(Res.string.all_share_description),
                        tint = TuripTheme.colors.gray03,
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }
        },
    )
}

@Composable
private fun TuripPlacesContent(
    listState: LazyListState,
    places: ImmutableList<TuripPlaceModel>,
    onMapClick: (map: MapModel) -> Unit,
    onTuripPlaceClick: (place: TuripPlaceModel) -> Unit,
    onDragStart: () -> Unit,
    onDragPlace: (from: Int, to: Int) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.all_total_place_count, places.size),
            style = TuripTheme.typography.info1,
            color = TuripTheme.colors.gray03,
            modifier = Modifier.padding(vertical = TuripTheme.spacing.medium),
        )

        TuripPlaces(
            listState = listState,
            places = places,
            onMapClick = onMapClick,
            onTuripPlaceClick = onTuripPlaceClick,
            onDragStart = onDragStart,
            onDragPlace = onDragPlace,
            onDragEnd = onDragEnd,
        )
    }
}

@Composable
private fun TuripPlaces(
    listState: LazyListState,
    places: ImmutableList<TuripPlaceModel>,
    onMapClick: (map: MapModel) -> Unit,
    onTuripPlaceClick: (place: TuripPlaceModel) -> Unit,
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
            ) { isDragging ->
                val interactionSource = remember { MutableInteractionSource() }
                val elevation by animateFloatAsState(if (isDragging) 8.0f else 0.0f)
                TuripPlaceItem(
                    place = place,
                    onMapClick = { onMapClick(place.mapModel) },
                    onTuripPlaceClick = { onTuripPlaceClick(place) },
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
