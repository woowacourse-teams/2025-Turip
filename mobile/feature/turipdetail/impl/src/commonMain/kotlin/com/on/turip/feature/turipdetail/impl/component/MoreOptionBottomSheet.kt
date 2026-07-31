package com.on.turip.feature.turipdetail.impl.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.*
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.component.NameEditorSheetContent
import com.on.turip.core.ui.model.namestatus.TuripNameStatusModel
import com.on.turip.feature.turipdetail.impl.TuripPlaceScreenMode
import com.on.turip.feature.turipdetail.impl.model.moreoption.MoreOptionIcon
import com.on.turip.feature.turipdetail.impl.model.moreoption.MoreOptionItem
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreOptionBottomSheet(
    screenMode: TuripPlaceScreenMode,
    onScreenModeChange: (turipPlaceScreenMode: TuripPlaceScreenMode) -> Unit,
    sheetState: SheetState,
    isDefault: Boolean,
    isTogetherTurip: Boolean,
    onDismiss: () -> Unit,
    onShareTuripByTextClick: () -> Unit,
    onShareTuripInvitationLinkClick: () -> Unit,
    onDeleteClick: () -> Unit,
    turipNameStatus: TuripNameStatusModel,
    initialTuripName: String,
    onNameChanged: (name: String) -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    val items: List<MoreOptionItem> =
        listOf(
            MoreOptionItem(
                title = stringResource(Res.string.turip_more_option_bottom_sheet_change_name),
                icon = MoreOptionIcon.Vector(Icons.Default.Create),
                color = TuripTheme.colors.black,
                onClick = { onScreenModeChange(TuripPlaceScreenMode.Edit) },
                enabled = !isDefault,
            ),
            MoreOptionItem(
                title = stringResource(Res.string.all_turip_share_by_text),
                icon = MoreOptionIcon.Vector(Icons.Default.Share),
                color = TuripTheme.colors.black,
                onClick = onShareTuripByTextClick,
            ),
            MoreOptionItem(
                title = stringResource(Res.string.all_turip_invite_by_link),
                icon = MoreOptionIcon.Vector(Icons.Default.Group),
                color = TuripTheme.colors.black,
                onClick = onShareTuripInvitationLinkClick,
                enabled = !isDefault,
            ),
            MoreOptionItem(
                title =
                    stringResource(
                        if (isTogetherTurip) {
                            Res.string.turip_more_option_bottom_sheet_leave
                        } else {
                            Res.string.turip_more_option_bottom_sheet_delete
                        },
                    ),
                icon =
                    MoreOptionIcon.Vector(
                        if (isTogetherTurip) {
                            Icons.AutoMirrored.Filled.Logout
                        } else {
                            Icons.Default.Delete
                        },
                    ),
                color = TuripTheme.colors.error,
                onClick = onDeleteClick,
                enabled = !isDefault,
            ),
        )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TuripTheme.colors.white,
        modifier = modifier,
    ) {
        AnimatedContent(
            targetState = screenMode,
            transitionSpec = {
                when (targetState) {
                    TuripPlaceScreenMode.Edit -> {
                        slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                    }

                    TuripPlaceScreenMode.MoreOption -> {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                            slideOutHorizontally { it } + fadeOut()
                    }
                }
            },
        ) { mode: TuripPlaceScreenMode ->
            when (mode) {
                TuripPlaceScreenMode.MoreOption -> {
                    Column {
                        items.forEachIndexed { index, item ->
                            MoreOptionRow(item = item)
                            if (index != items.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = TuripTheme.spacing.extraLarge),
                                )
                            }
                        }
                    }
                }

                TuripPlaceScreenMode.Edit -> {
                    NameEditorSheetContent(
                        title = stringResource(Res.string.bottom_sheet_turip_modify_title),
                        initialTuripName = initialTuripName,
                        onBack = { onScreenModeChange(TuripPlaceScreenMode.MoreOption) },
                        turipNameStatus = turipNameStatus,
                        isConfirmEnabled = turipNameStatus.isConfirmEnabled,
                        onNameChanged = onNameChanged,
                        onConfirmClick = onConfirmClick,
                        focusRequester = focusRequester,
                    )
                }
            }
        }
    }
}

@Composable
private fun MoreOptionRow(
    item: MoreOptionItem,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(enabled = item.enabled, onClick = item.onClick)
                .then(if (!item.enabled) Modifier.alpha(0.4f) else Modifier)
                .padding(
                    horizontal = TuripTheme.spacing.extraHuge,
                    vertical = TuripTheme.spacing.large,
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (val icon = item.icon) {
            is MoreOptionIcon.Vector -> {
                Icon(
                    imageVector = icon.imageVector,
                    contentDescription = null,
                    tint = item.color,
                    modifier = Modifier.size(18.dp),
                )
            }

            is MoreOptionIcon.Resource -> {
                Icon(
                    painter = painterResource(icon.resId),
                    contentDescription = null,
                    tint = item.color,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        Text(
            text = item.title,
            style = TuripTheme.typography.title1.copy(fontWeight = FontWeight.Normal),
            color = item.color,
            modifier = Modifier.padding(start = TuripTheme.spacing.large),
        )
    }
}
