package com.on.turip.feature.trip.impl.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.*
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.util.formatResource
import com.on.turip.feature.trip.impl.model.MapModel
import com.on.turip.feature.trip.impl.model.PlaceModel
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun PlaceItem(
    placeModel: PlaceModel,
    onTimeLineClick: (timeLine: Int) -> Unit,
    onMapClick: (mapModel: MapModel) -> Unit,
    onTuripPlaceClick: (id: Long, placeName: String) -> Unit,
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
                    bottom = TuripTheme.spacing.medium,
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

        Spacer(modifier = Modifier.height(TuripTheme.spacing.small))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            PlaceActionItem(
                text = stringResource(Res.string.trip_place_time_line).formatResource(placeModel.timeLine),
                drawableRes = Res.drawable.ic_play_button,
                useTint = true,
                onClick = { onTimeLineClick(placeModel.seekTimeSeconds) },
            )

            VerticalDivider(modifier = Modifier.height(TuripTheme.spacing.extraExtraLarge))

            PlaceActionItem(
                text = stringResource(Res.string.trip_place_map),
                drawableRes = placeModel.mapModel.drawableRes,
                useTint = placeModel.mapModel.enableTint,
                onClick = { onMapClick(placeModel.mapModel) },
            )

            VerticalDivider(modifier = Modifier.height(TuripTheme.spacing.extraExtraLarge))

            PlaceActionItem(
                text = stringResource(Res.string.trip_turip_place),
                iconTint = if (placeModel.isTuripPlace) TuripTheme.colors.primary else TuripTheme.colors.gray04,
                drawableRes = if (placeModel.isTuripPlace) Res.drawable.btn_turip_place_selected else Res.drawable.btn_turip_place_normal,
                useTint = true,
                onClick = { onTuripPlaceClick(placeModel.id, placeModel.name) },
            )
        }
    }
}

@Composable
private fun PlaceActionItem(
    text: String,
    drawableRes: DrawableResource,
    useTint: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = TuripTheme.colors.gray04,
    iconTint: Color = TuripTheme.colors.gray04,
    iconSize: Dp = 24.dp,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scaleAnimation",
    )

    Row(
        modifier =
            modifier
                .graphicsLayer(scaleX = scale, scaleY = scale)
                .clip(TuripTheme.shape.container)
                .clickable(
                    onClick = onClick,
                    interactionSource = interactionSource,
                    indication = ripple(color = TuripTheme.colors.gray03),
                ).padding(
                    horizontal = TuripTheme.spacing.medium,
                    vertical = TuripTheme.spacing.extraSmall,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraSmall),
    ) {
        Icon(
            painter = painterResource(drawableRes),
            contentDescription = null,
            tint = if (useTint) iconTint else Color.Unspecified,
            modifier = Modifier.size(iconSize),
        )
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
            isTuripPlace = true,
            category = "💻 코딩맛집",
            mapLink = "kakao.com/123123",
            timeLine = "01:03",
        )
    TuripTheme {
        PlaceItem(
            placeModel = model,
            onTimeLineClick = { },
            onMapClick = { },
            onTuripPlaceClick = { _, _ -> },
            modifier = Modifier.padding(TuripTheme.spacing.extraLarge),
        )
    }
}
