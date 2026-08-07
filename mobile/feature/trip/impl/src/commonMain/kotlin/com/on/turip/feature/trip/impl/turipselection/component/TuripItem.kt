package com.on.turip.feature.trip.impl.turipselection.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.btn_turip_normal
import com.on.turip.core.designsystem.generated.resources.btn_turip_selected
import com.on.turip.core.designsystem.generated.resources.ic_turip
import com.on.turip.core.designsystem.generated.resources.trip_detail_bottom_sheet_turip_place_select_description
import com.on.turip.core.designsystem.generated.resources.trip_detail_bottom_sheet_turip_place_unselect_description
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.trip.impl.turipselection.TuripSelectionModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TuripItem(
    turip: TuripSelectionModel,
    onItemClick: () -> Unit,
    onTuripPlaceClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(vertical = TuripTheme.spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
    ) {
        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(start = TuripTheme.spacing.large)
                    .padding(vertical = TuripTheme.spacing.extraSmall)
                    .clip(TuripTheme.shape.container)
                    .clickable(onClick = onItemClick),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .background(
                            color = TuripTheme.colors.gray03,
                            shape = TuripTheme.shape.container,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_turip),
                    contentDescription = null,
                    tint = TuripTheme.colors.white,
                    modifier = Modifier.size(24.dp),
                )
            }
            Text(
                text = turip.name,
                style = TuripTheme.typography.title2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        IconButton(
            onClick = onTuripPlaceClick,
            modifier = Modifier.padding(end = TuripTheme.spacing.small),
        ) {
            Icon(
                painter =
                    painterResource(
                        if (turip.isSelected) {
                            Res.drawable.btn_turip_selected
                        } else {
                            Res.drawable.btn_turip_normal
                        },
                    ),
                contentDescription =
                    stringResource(
                        if (turip.isSelected) {
                            Res.string.trip_detail_bottom_sheet_turip_place_unselect_description
                        } else {
                            Res.string.trip_detail_bottom_sheet_turip_place_select_description
                        },
                    ),
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}
