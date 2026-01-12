package com.on.turip.ui.compose.trip.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.domain.trip.MapType
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.trip.model.PlaceModel

@Composable
fun PlaceItem(
    placeModel: PlaceModel,
    onTimeLineClick: () -> Unit,
    onMapClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = TuripTheme.colors.border,
                    shape = TuripTheme.shape.container,
                ).background(
                    color = TuripTheme.colors.container,
                    shape = TuripTheme.shape.container,
                ).padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
    ) {
        Text(
            text = placeModel.turipCategory,
            style = TuripTheme.typography.info1,
            color = TuripTheme.colors.gray05,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = placeModel.name,
            style = TuripTheme.typography.title2,
            color = TuripTheme.colors.gray04,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            PlaceActionItem(
                text = stringResource(R.string.trip_place_time_line, placeModel.timeLine),
                drawableRes = R.drawable.ic_play_button,
                useTint = true,
                onClick = onTimeLineClick,
            )

            VerticalDivider(height = 24.dp)

            PlaceActionItem(
                text = stringResource(R.string.trip_place_map),
                drawableRes =
                    when (placeModel.mapModel.type) {
                        MapType.KAKAO -> R.drawable.btn_kakao_map_basic
                        MapType.GOOGLE -> R.drawable.btn_google_map_basic
                        MapType.NONE -> R.drawable.btn_map_link
                    },
                useTint =
                    when (placeModel.mapModel.type) {
                        MapType.KAKAO -> false
                        MapType.GOOGLE -> false
                        MapType.NONE -> true
                    },
                onClick = onMapClick,
            )

            VerticalDivider(height = 24.dp)

            PlaceActionItem(
                text = stringResource(R.string.trip_place_favorite),
                drawableRes = if (placeModel.isFavorite) R.drawable.btn_favorite_selected else R.drawable.btn_favorite_normal,
                useTint = true,
                onClick = onFavoriteClick,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PlaceItemPreview() {
    val model =
        PlaceModel(
            id = 1L,
            name = "우아한테크코스",
            isFavorite = true,
            category = "💻 코딩맛집",
            mapLink = "kakao.com/123123",
            timeLine = "01:03",
        )
    TuripTheme {
        PlaceItem(
            placeModel = model,
            onTimeLineClick = { },
            onMapClick = { },
            onFavoriteClick = { },
            modifier = Modifier.padding(20.dp),
        )
    }
}
