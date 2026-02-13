package com.on.turip.ui.compose.trip.bottomsheet.component

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
import com.on.turip.ui.compose.trip.bottomsheet.model.TuripPlaceModel

@Composable
fun TuripPlaceItem(
    place: TuripPlaceModel,
    onMapClick: () -> Unit,
    onTuripPlaceClick: () -> Unit,
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

            TuripPlaceButton(
                drawableRes = place.mapModel.drawableRes,
                useTint = place.mapModel.enableTint,
                onClick = onMapClick,
            )

            TuripPlaceButton(
                drawableRes = R.drawable.btn_turip_place_selected,
                useTint = true,
                onClick = onTuripPlaceClick,
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
private fun TuripPlaceButton(
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

private data class TuripPlaceItemPreviewState(
    val name: String,
    val place: TuripPlaceModel,
)

private val baseTuripPlaceModel =
    TuripPlaceModel(
        0L,
        0L,
        0L,
        "",
        false,
        LatLng(0.0, 0.0),
        "",
        "",
    )

private class TuripPlaceItemPreviewProvider : PreviewParameterProvider<TuripPlaceItemPreviewState> {
    override val values: Sequence<TuripPlaceItemPreviewState> =
        sequenceOf(
            TuripPlaceItemPreviewState(
                name = "Normal",
                place = baseTuripPlaceModel.copy(name = "장소명", category = "카테고리"),
            ),
            TuripPlaceItemPreviewState(
                name = "Long Place Name",
                place =
                    baseTuripPlaceModel.copy(
                        name = "이름이 아주아주 길어서 말줄임이 제대로 되는지 확인하기 위한 장소명",
                        category = "카테고리",
                    ),
            ),
            TuripPlaceItemPreviewState(
                name = "Long Category",
                place =
                    baseTuripPlaceModel.copy(
                        name = "이름이 아주아주 길어서 말줄임이 제대로 되는지 확인하기 위한 장소명",
                        category = "아주아주아주아주아주아주아주아주아주 긴 카테고리 이름",
                        mapLink = "google.com",
                    ),
            ),
            TuripPlaceItemPreviewState(
                name = "Not Favorite (Edge)",
                place =
                    baseTuripPlaceModel.copy(
                        name = "즐겨찾기 해제 상태",
                        isTuripPlace = false,
                        category = "카테고리",
                        mapLink = "kakao.com",
                    ),
            ),
        )
}

@Preview(showBackground = true)
@Composable
private fun TuripPlaceItemPreview(
    @PreviewParameter(TuripPlaceItemPreviewProvider::class)
    state: TuripPlaceItemPreviewState,
) {
    TuripTheme {
        TuripPlaceItem(
            place = state.place,
            onMapClick = {},
            onTuripPlaceClick = {},
        )
    }
}
