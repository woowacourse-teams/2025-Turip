package com.on.turip.feature.turipdetail.impl.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.btn_turip_place_selected
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.turipdetail.impl.model.TuripPlaceModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun TuripPlaceItem(
    place: TuripPlaceModel,
    onMapClick: () -> Unit,
    onTuripPlaceClick: () -> Unit,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {},
) {
    Surface(
        onClick = onItemClick,
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
                imageVector = Icons.Outlined.Menu,
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
                drawableRes = Res.drawable.btn_turip_place_selected,
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
    drawableRes: DrawableResource,
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

@Preview(showBackground = true)
@Composable
private fun TuripPlaceItemPreview() {
    TuripTheme {
        TuripPlaceItem(
            place =
                TuripPlaceModel.Idle.copy(
                    name = "장소명",
                    category = "카테고리",
                ),
            onItemClick = {},
            onMapClick = {},
            onTuripPlaceClick = {},
        )
    }
}
