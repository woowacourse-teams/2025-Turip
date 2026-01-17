package com.on.turip.ui.compose.trip.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.domain.trip.MapType
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.trip.model.MapModel
import com.on.turip.ui.compose.trip.model.PlaceModel

@Composable
fun PlaceItem(
    placeModel: PlaceModel,
    onTimeLineClick: (timeLine: Int) -> Unit,
    onMapClick: (mapModel: MapModel) -> Unit,
    onFavoriteClick: (id: Long) -> Unit,
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
                ).padding(
                    start = TuripTheme.spacing.large,
                    end = TuripTheme.spacing.large,
                    top = TuripTheme.spacing.medium,
                    bottom = TuripTheme.spacing.large,
                ),
    ) {
        Text(
            text = placeModel.turipCategory,
            style = TuripTheme.typography.info1,
            color = TuripTheme.colors.gray05,
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.extraSmall))

        Text(
            text = placeModel.name,
            style = TuripTheme.typography.title2,
            color = TuripTheme.colors.gray04,
        )

        Spacer(modifier = Modifier.height(TuripTheme.spacing.medium))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            PlaceActionItem(
                text = stringResource(R.string.trip_place_time_line, placeModel.timeLine),
                drawableRes = R.drawable.ic_play_button,
                useTint = true,
                onClick = { onTimeLineClick(placeModel.seekTimeSeconds) },
            )

            VerticalDivider(modifier = Modifier.height(TuripTheme.spacing.extraExtraLarge))

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
                onClick = { onMapClick(placeModel.mapModel) },
            )

            VerticalDivider(modifier = Modifier.height(TuripTheme.spacing.extraExtraLarge))

            PlaceActionItem(
                text = stringResource(R.string.trip_place_favorite),
                iconTint = if (placeModel.isFavorite) TuripTheme.colors.primary else TuripTheme.colors.gray04,
                drawableRes = if (placeModel.isFavorite) R.drawable.btn_favorite_selected else R.drawable.btn_favorite_normal,
                useTint = true,
                onClick = { onFavoriteClick(placeModel.id) },
            )
        }
    }
}

@Composable
private fun PlaceActionItem(
    text: String,
    @DrawableRes drawableRes: Int,
    useTint: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = TuripTheme.colors.gray04,
    iconTint: Color = TuripTheme.colors.gray04,
    iconSize: Dp = 24.dp,
) {
    Row(
        modifier =
            modifier
                .clickable(onClick = onClick)
                .padding(horizontal = TuripTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraSmall),
    ) {
        if (useTint) {
            Icon(
                painter = painterResource(drawableRes),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(iconSize),
            )
        } else {
            Image(
                painter = painterResource(drawableRes),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
            )
        }
        Text(
            text = text,
            style = TuripTheme.typography.info1,
            color = textColor,
        )
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
            modifier = Modifier.padding(TuripTheme.spacing.extraLarge),
        )
    }
}
