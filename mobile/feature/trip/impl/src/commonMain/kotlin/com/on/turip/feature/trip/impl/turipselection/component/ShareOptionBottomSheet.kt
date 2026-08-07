package com.on.turip.feature.trip.impl.turipselection.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_close_description
import com.on.turip.core.designsystem.generated.resources.all_turip_invite_by_link
import com.on.turip.core.designsystem.generated.resources.all_turip_share_by_text
import com.on.turip.core.designsystem.theme.TuripTheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareOptionBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onShareByTextClick: () -> Unit,
    onInviteLinkClick: () -> Unit,
    isInviteLinkEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = TuripTheme.colors.white,
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier.padding(
                    top = TuripTheme.spacing.medium,
                    bottom = TuripTheme.spacing.large,
                ),
        ) {
            ShareOptionCloseButton(
                onCloseClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            )

            ShareOptionRow(
                icon = Icons.Default.Share,
                title = stringResource(Res.string.all_turip_share_by_text),
                onClick = onShareByTextClick,
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = TuripTheme.spacing.extraHuge),
                color = TuripTheme.colors.gray01,
            )

            ShareOptionRow(
                icon = Icons.Default.Group,
                title = stringResource(Res.string.all_turip_invite_by_link),
                onClick = onInviteLinkClick,
                enabled = isInviteLinkEnabled,
            )
        }
    }
}

@Composable
private fun ShareOptionCloseButton(
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.padding(end = TuripTheme.spacing.medium)) {
        IconButton(
            onClick = onCloseClick,
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .size(32.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(Res.string.all_close_description),
                modifier = Modifier.size(16.dp),
                tint = TuripTheme.colors.gray04,
            )
        }
    }
}

@Composable
private fun ShareOptionRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(enabled = enabled, onClick = onClick)
                .then(if (!enabled) Modifier.alpha(0.4f) else Modifier)
                .padding(
                    horizontal = TuripTheme.spacing.extraHuge,
                    vertical = TuripTheme.spacing.large,
                ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TuripTheme.colors.black,
            modifier = Modifier.size(18.dp),
        )

        Text(
            text = title,
            style = TuripTheme.typography.title1.copy(fontWeight = FontWeight.Normal),
            modifier = Modifier.padding(start = TuripTheme.spacing.large),
        )
    }
}
