package com.on.turip.ui.compose.trip.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.common.collect.ImmutableList
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.trip.model.PlaceModel

@Composable
fun Places(
    state: LazyListState,
    places: ImmutableList<PlaceModel>,
    onTimeLineClick: (id: Long) -> Unit,
    onMapClick: (id: Long) -> Unit,
    onFavoriteClick: (id: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = state,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        modifier = modifier,
    ) {
        items(items = places, key = { it.id }) { place: PlaceModel ->
            PlaceItem(
                placeModel = place,
                onTimeLineClick = { onTimeLineClick(place.id) },
                onMapClick = { onMapClick(place.id) },
                onFavoriteClick = { onFavoriteClick(place.id) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PlacesPreview() {
    val listState = rememberLazyListState()
    val places =
        ImmutableList.of(
            PlaceModel(1L, "우테코", true, "01:21", "핫플", "google.com"),
            PlaceModel(2L, "배민", true, "02:02", "핫플1", "kakao.com"),
            PlaceModel(3L, "DH", false, "03:33", "핫플2", "none"),
        )
    TuripTheme {
        Places(
            state = listState,
            places = places,
            onTimeLineClick = { },
            onMapClick = { },
            onFavoriteClick = { },
        )
    }
}
