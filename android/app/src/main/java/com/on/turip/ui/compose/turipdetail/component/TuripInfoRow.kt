package com.on.turip.ui.compose.turipdetail.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun TuripInfoRow(
    savedPlaceCount: Int,
    participantCount: Int,
    showParticipant: Boolean,
    onClickMembers: () -> Unit,
    onClickPlaces: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = TuripTheme.spacing.large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (savedPlaceCount > 0) {
            TuripInfoItem(
                icon = Icons.Default.LocationOn,
                label = stringResource(R.string.trip_detail_saved_places_label),
                count = savedPlaceCount,
                onClick = onClickPlaces,
            )
        }
        if (showParticipant) {
            TuripInfoItem(
                icon = Icons.Default.Group,
                label = stringResource(R.string.trip_detail_participant_count_label),
                count = participantCount,
                onClick = onClickMembers,
            )
        }
    }
}

@Composable
private fun TuripInfoItem(
    icon: ImageVector,
    label: String,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFAAAAAA),
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(TuripTheme.spacing.small))
        Text(
            text = "$label  $count",
            style = TuripTheme.typography.title3,
            color = TuripTheme.colors.gray02,
        )
    }
}

@Composable
@Preview
private fun TuripInfoItemPreview() {
    TuripTheme {
        TuripInfoRow(
            savedPlaceCount = 10,
            participantCount = 10,
            showParticipant = true,
            onClickMembers = {},
            onClickPlaces = {},
        )
    }
}
