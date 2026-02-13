package com.on.turip.ui.compose.trip.bottomsheet.component

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.main.favorite.model.TuripModel

@Composable
fun TuripItem(
    turip: TuripModel,
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
                    painter = painterResource(R.drawable.ic_turip),
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
                    if (turip.isSelected) {
                        painterResource(R.drawable.btn_turip_selected)
                    } else {
                        painterResource(R.drawable.btn_turip_normal)
                    },
                contentDescription =
                    if (turip.isSelected) {
                        stringResource(R.string.trip_detail_bottom_sheet_turip_place_unselect_description)
                    } else {
                        stringResource(R.string.trip_detail_bottom_sheet_turip_place_select_description)
                    },
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

private class TuripItemPreviewParameterProvider : PreviewParameterProvider<TuripModel> {
    override val values =
        sequenceOf(
            TuripModel(
                id = 1L,
                name = "튜립에 포함된 상태",
                placeCount = 5,
                isSelected = true,
            ),
            TuripModel(
                id = 2L,
                name = "튜립에 포함되지 않은 상태",
                placeCount = 0,
                isSelected = false,
            ),
            TuripModel(
                id = 3L,
                name = "이름이 긴 여행 폴더명 서울 여행 서울 여행 서울 여행 서울 여행 서울 여행",
                placeCount = 10,
                isSelected = true,
            ),
        )
}

@Preview(showBackground = true)
@Composable
private fun TuripItemPreview(
    @PreviewParameter(TuripItemPreviewParameterProvider::class) turip: TuripModel,
) {
    TuripTheme {
        TuripItem(
            turip = turip,
            onItemClick = { },
            onTuripPlaceClick = { },
        )
    }
}
