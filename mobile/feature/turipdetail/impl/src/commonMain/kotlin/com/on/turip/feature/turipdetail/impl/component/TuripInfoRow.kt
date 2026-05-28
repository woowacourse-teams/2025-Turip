package com.on.turip.feature.turipdetail.impl.component

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
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.theme.TuripTheme

@Composable
fun TuripInfoRow(
    placeCount: Int,
    memberCount: Int,
    isShared: Boolean,
    onMembersClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = TuripTheme.spacing.large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.large),
    ) {
        InfoItem(
            icon = Icons.Default.LocationOn,
            label = "저장한 장소",
            count = placeCount,
            onClick = {},
        )
        if (isShared) {
            InfoItem(
                icon = Icons.Default.Group,
                label = "참여자",
                count = memberCount,
                onClick = onMembersClick,
            )
        }
    }
}

@Composable
private fun InfoItem(
    icon: ImageVector,
    label: String,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
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
