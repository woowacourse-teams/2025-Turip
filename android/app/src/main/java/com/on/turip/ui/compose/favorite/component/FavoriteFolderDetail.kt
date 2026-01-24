package com.on.turip.ui.compose.favorite.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    modifier: Modifier = Modifier,
) {
    val hasPlaces = places.isNotEmpty()

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
                places = places,
                onMapClick = onMapClick,
                onFavoriteClick = onFavoriteClick,
                modifier = Modifier.padding(horizontal = TuripTheme.spacing.large),
            )
        } else {
            EmptyFavoritePlaces(
                modifier =
                    Modifier
                        .fillMaxWidth()
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
                    modifier = Modifier.size(40.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_share),
                        contentDescription = stringResource(R.string.all_share_description),
                        tint = TuripTheme.colors.gray03,
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(40.dp))
            }
        },
    )
}

@Composable
private fun FavoritePlacesContent(
    places: ImmutableList<FavoritePlaceModel>,
    onMapClick: (map: MapModel) -> Unit,
    onFavoriteClick: (place: FavoritePlaceModel) -> Unit,
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
            places = places,
            onMapClick = onMapClick,
            onFavoriteClick = onFavoriteClick,
        )
    }
}

@Composable
private fun FavoritePlaces(
    places: ImmutableList<FavoritePlaceModel>,
    onMapClick: (map: MapModel) -> Unit,
    onFavoriteClick: (place: FavoritePlaceModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
        contentPadding = PaddingValues(bottom = TuripTheme.spacing.medium),
        modifier = modifier,
    ) {
        items(items = places, key = { it.favoritePlaceId }) { place ->
            FavoritePlaceItem(
                place = place,
                onMapClick = { onMapClick(place.mapModel) },
                onFavoriteClick = { onFavoriteClick(place) },
            )
        }
    }
}

@Composable
fun EmptyFavoritePlaces(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
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
                        FavoritePlaceModel(
                            1L,
                            1L,
                            1L,
                            "장소명1",
                            true,
                            LatLng(0.0, 0.0),
                            "카테고리1",
                            "url",
                        ),
                        FavoritePlaceModel(
                            2L,
                            2L,
                            2L,
                            "장소명2",
                            true,
                            LatLng(0.0, 0.0),
                            "카테고리2",
                            "url",
                        ),
                    ),
            ),
            FavoriteFolderDetailPreviewState(
                name = "Long Title",
                folderName = "폴더 이름이 정말 말도 안 되게 길어질 경우 UI가 어떻게 보일까요?",
                places =
                    persistentListOf(
                        FavoritePlaceModel(
                            1L,
                            1L,
                            1L,
                            "아주아주아주아주아주매우매우매우매우 긴 장소 이름",
                            true,
                            LatLng(0.0, 0.0),
                            "카테고리",
                            "url",
                        ),
                    ),
            ),
            FavoriteFolderDetailPreviewState(
                name = "Many Places",
                folderName = "장소 많은 폴더",
                places =
                    (1..20)
                        .map {
                            FavoritePlaceModel(
                                favoritePlaceId = it.toLong(),
                                order = it.toLong(),
                                placeId = it.toLong(),
                                name = "장소명 $it",
                                isFavorite = true,
                                latLng = LatLng(0.0, 0.0),
                                category = "카테고리",
                                mapLink = "url",
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
        )
    }
}
