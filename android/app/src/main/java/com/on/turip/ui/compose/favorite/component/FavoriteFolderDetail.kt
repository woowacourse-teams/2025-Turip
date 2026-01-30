package com.on.turip.ui.compose.favorite.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.favorite.model.FavoritePlaceModel
import com.on.turip.ui.compose.favorite.util.reorderable.ReorderableItem
import com.on.turip.ui.compose.favorite.util.reorderable.rememberReorderableLazyColumnState
import com.on.turip.ui.compose.trip.model.MapModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

@Composable
fun FavoriteFolderDetail(
    folderName: String,
    places: ImmutableList<FavoritePlaceModel>,
    onMapClick: (map: MapModel) -> Unit,
    onFavoriteClick: (place: FavoritePlaceModel) -> Unit,
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
            folderName = folderName,
            isVisibleShare = hasPlaces,
            onBackClick = onBackClick,
            onShareClick = onShareClick,
        )

        if (hasPlaces) {
            FavoritePlacesContent(
                listState = listState,
                places = places,
                onMapClick = onMapClick,
                onFavoriteClick = onFavoriteClick,
                onDragStart = onDragStart,
                onDragPlace = onDragPlace,
                onDragEnd = onDragEnd,
                modifier = Modifier.padding(horizontal = TuripTheme.spacing.large),
            )
        } else {
            EmptyFavoritePlaces(
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
    folderName: String,
    isVisibleShare: Boolean,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
) {
    FavoritePlaceFolderBottomSheetHeader(
        title = folderName,
        navigation = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.btn_chevron_left),
                    contentDescription = stringResource(R.string.all_back_description),
                    tint = TuripTheme.colors.gray03,
                )
            }
        },
        actions = {
            if (isVisibleShare) {
                IconButton(
                    onClick = onShareClick,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_share),
                        contentDescription = stringResource(R.string.all_share_description),
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
private fun FavoritePlacesContent(
    listState: LazyListState,
    places: ImmutableList<FavoritePlaceModel>,
    onMapClick: (map: MapModel) -> Unit,
    onFavoriteClick: (place: FavoritePlaceModel) -> Unit,
    onDragStart: () -> Unit,
    onDragPlace: (from: Int, to: Int) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.all_total_place_count, places.size),
            style = TuripTheme.typography.info1,
            color = TuripTheme.colors.gray03,
            modifier = Modifier.padding(vertical = TuripTheme.spacing.medium),
        )

        FavoritePlaces(
            listState = listState,
            places = places,
            onMapClick = onMapClick,
            onFavoriteClick = onFavoriteClick,
            onDragStart = onDragStart,
            onDragPlace = onDragPlace,
            onDragEnd = onDragEnd,
        )
    }
}

@Composable
private fun FavoritePlaces(
    listState: LazyListState,
    places: ImmutableList<FavoritePlaceModel>,
    onMapClick: (map: MapModel) -> Unit,
    onFavoriteClick: (place: FavoritePlaceModel) -> Unit,
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
        modifier = modifier,
    ) {
        items(items = places, key = { it.favoritePlaceId }) { place ->
            ReorderableItem(
                state = reorderableLazyColumnState,
                key = place.favoritePlaceId,
            ) { isDragging: Boolean ->
                val interactionSource = remember { MutableInteractionSource() }
                val elevation by animateFloatAsState(if (isDragging) 8.0f else 0.0f)
                FavoritePlaceItem(
                    place = place,
                    onMapClick = { onMapClick(place.mapModel) },
                    onFavoriteClick = { onFavoriteClick(place) },
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
fun EmptyFavoritePlaces(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.mascot),
                contentDescription = stringResource(R.string.all_mascot_description),
            )

            Spacer(modifier = Modifier.height(TuripTheme.spacing.extraLarge))

            Text(
                text = stringResource(R.string.favorite_place_empty_suggest_title),
                style = TuripTheme.typography.title1,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(TuripTheme.spacing.small))

            Text(
                text = stringResource(R.string.favorite_place_empty_suggest_description),
                style = TuripTheme.typography.title2,
                color = TuripTheme.colors.gray03,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private data class FavoriteFolderDetailPreviewState(
    val name: String,
    val folderName: String,
    val places: ImmutableList<FavoritePlaceModel>,
)

private val baseFavoritePlaceModel =
    FavoritePlaceModel(
        0L,
        0L,
        0L,
        "",
        false,
        LatLng(0.0, 0.0),
        "",
        "",
    )

private class FavoriteFolderDetailPreviewProvider : PreviewParameterProvider<FavoriteFolderDetailPreviewState> {
    override val values: Sequence<FavoriteFolderDetailPreviewState> =
        sequenceOf(
            FavoriteFolderDetailPreviewState(
                name = "Empty",
                folderName = "빈 폴더",
                places = persistentListOf(),
            ),
            FavoriteFolderDetailPreviewState(
                name = "Normal",
                folderName = "서울 여행",
                places =
                    persistentListOf(
                        baseFavoritePlaceModel.copy(
                            favoritePlaceId = 1L,
                            name = "장소명1",
                            category = "카테고리1",
                        ),
                        baseFavoritePlaceModel.copy(
                            favoritePlaceId = 2L,
                            name = "장소명2",
                            category = "카테고리2",
                        ),
                    ),
            ),
            FavoriteFolderDetailPreviewState(
                name = "Long Title",
                folderName = "폴더 이름이 정말 말도 안 되게 길어질 경우 UI가 어떻게 보일까요?",
                places =
                    persistentListOf(
                        baseFavoritePlaceModel.copy(
                            favoritePlaceId = 1L,
                            name = "아주아주아주아주아주매우매우매우매우 긴 장소 이름",
                            category = "카테고리",
                        ),
                    ),
            ),
            FavoriteFolderDetailPreviewState(
                name = "Many Places",
                folderName = "장소 많은 폴더",
                places =
                    (1..20)
                        .map {
                            baseFavoritePlaceModel.copy(
                                favoritePlaceId = it.toLong(),
                                name = "장소명 $it",
                                category = "카테고리 $it",
                            )
                        }.toPersistentList(),
            ),
        )
}

@Preview(showBackground = true)
@Composable
private fun FavoriteFolderDetail_Preview(
    @PreviewParameter(FavoriteFolderDetailPreviewProvider::class)
    state: FavoriteFolderDetailPreviewState,
) {
    TuripTheme {
        FavoriteFolderDetail(
            folderName = state.folderName,
            places = state.places,
            onMapClick = {},
            onFavoriteClick = {},
            onBackClick = {},
            onShareClick = {},
            onDragStart = {},
            onDragPlace = { _, _ -> },
            onDragEnd = { },
        )
    }
}
