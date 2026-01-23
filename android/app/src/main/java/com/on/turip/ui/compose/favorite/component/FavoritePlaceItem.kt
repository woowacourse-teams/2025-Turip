package com.on.turip.ui.compose.favorite.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.favorite.model.FavoritePlaceModel

@Composable
fun FavoritePlaceItem(
    place: FavoritePlaceModel,
    onMapClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = TuripTheme.shape.container,
        border = BorderStroke(1.dp, TuripTheme.colors.gray02),
        color = TuripTheme.colors.container,
        modifier = modifier.heightIn(min = 84.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraSmall),
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier.padding(
                    horizontal = TuripTheme.spacing.medium,
                    vertical = TuripTheme.spacing.extraSmall,
                ),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_hamburger),
                contentDescription = null,
                modifier = Modifier.padding(TuripTheme.spacing.small),
            )

            CategoryAndPlaceName(
                categoryName = place.turipCategory,
                placeName = place.name,
                modifier = Modifier.weight(1f),
            )

            FavoritePlaceButton(
                drawableRes = place.mapModel.drawableRes,
                useTint = place.mapModel.enableTint,
                onClick = onMapClick,
            )

            FavoritePlaceButton(
                drawableRes = R.drawable.btn_favorite_selected,
                useTint = true,
                onClick = onFavoriteClick,
                iconTint = TuripTheme.colors.primary,
            )
        }
    }
}

@Composable
private fun CategoryAndPlaceName(
    categoryName: String,
    placeName: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraSmall),
    ) {
        Text(
            text = categoryName,
            style = TuripTheme.typography.title3,
            color = TuripTheme.colors.gray05,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = placeName,
            style = TuripTheme.typography.title1,
            color = TuripTheme.colors.gray04,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun FavoritePlaceButton(
    @DrawableRes drawableRes: Int,
    useTint: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = TuripTheme.colors.gray03,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(40.dp),
    ) {
        Icon(
            painter = painterResource(drawableRes),
            contentDescription = null,
            tint = if (useTint) iconTint else Color.Unspecified,
            modifier = Modifier.size(24.dp),
        )
    }
}

private data class FavoritePlaceItemPreviewState(
    val name: String,
    val place: FavoritePlaceModel,
)

private class FavoritePlaceItemPreviewProvider : PreviewParameterProvider<FavoritePlaceItemPreviewState> {
    override val values: Sequence<FavoritePlaceItemPreviewState> =
        sequenceOf(
            FavoritePlaceItemPreviewState(
                name = "Normal",
                place =
                    FavoritePlaceModel(
                        favoritePlaceId = 1L,
                        order = 1L,
                        placeId = 1L,
                        name = "장소명",
                        isFavorite = true,
                        latLng = LatLng(0.0, 0.0),
                        category = "카테고리",
                        mapLink = "url",
                    ),
            ),
            FavoritePlaceItemPreviewState(
                name = "Long Place Name",
                place =
                    FavoritePlaceModel(
                        favoritePlaceId = 2L,
                        order = 2L,
                        placeId = 2L,
                        name = "이름이 아주아주 길어서 말줄임이 제대로 되는지 확인하기 위한 장소명",
                        isFavorite = true,
                        latLng = LatLng(0.0, 0.0),
                        category = "카테고리",
                        mapLink = "url",
                    ),
            ),
            FavoritePlaceItemPreviewState(
                name = "Long Category",
                place =
                    FavoritePlaceModel(
                        favoritePlaceId = 3L,
                        order = 3L,
                        placeId = 3L,
                        name = "장소명",
                        isFavorite = true,
                        latLng = LatLng(0.0, 0.0),
                        category = "아주아주아주아주아주아주아주아주아주 긴 카테고리 이름",
                        mapLink = "google.com",
                    ),
            ),
            FavoritePlaceItemPreviewState(
                name = "Not Favorite (Edge)",
                place =
                    FavoritePlaceModel(
                        favoritePlaceId = 4L,
                        order = 4L,
                        placeId = 4L,
                        name = "즐겨찾기 해제 상태",
                        isFavorite = false,
                        latLng = LatLng(0.0, 0.0),
                        category = "카테고리",
                        mapLink = "kakao.com",
                    ),
            ),
        )
}

@Preview(showBackground = true)
@Composable
private fun FavoritePlaceItem_Preview(
    @PreviewParameter(FavoritePlaceItemPreviewProvider::class)
    state: FavoritePlaceItemPreviewState,
) {
    TuripTheme {
        FavoritePlaceItem(
            place = state.place,
            onMapClick = {},
            onFavoriteClick = {},
        )
    }
}
