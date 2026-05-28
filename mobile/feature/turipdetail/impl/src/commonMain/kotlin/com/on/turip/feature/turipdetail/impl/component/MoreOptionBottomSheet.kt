package com.on.turip.feature.turipdetail.impl.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.theme.TuripTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreOptionBottomSheet(
    isShared: Boolean,
    isDefault: Boolean,
    onRenameClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = TuripTheme.colors.white,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = TuripTheme.spacing.extraLarge)
                .padding(bottom = TuripTheme.spacing.extraLarge),
        ) {
            if (!isDefault) {
                OptionItem(
                    icon = Icons.Default.Edit,
                    label = "이름 변경",
                    onClick = onRenameClick,
                )

                Spacer(modifier = Modifier.height(TuripTheme.spacing.medium))

                OptionItem(
                    icon = if (isShared) Icons.Default.ExitToApp else Icons.Default.Delete,
                    label = if (isShared) "튜립 나가기" else "튜립 삭제",
                    onClick = onDeleteClick,
                    color = TuripTheme.colors.error,
                )
            }
        }
    }
}

@Composable
private fun OptionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color = TuripTheme.colors.black,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = TuripTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(TuripTheme.spacing.large))
        Text(text = label, style = TuripTheme.typography.title3, color = color)
    }
}
