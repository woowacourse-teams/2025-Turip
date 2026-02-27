package com.on.turip.ui.compose.favorite.component

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
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.component.NameEditorSheetContent
import com.on.turip.ui.compose.designsystem.model.TuripNameStatusModel
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.favorite.TuripPlaceScreenMode
import com.on.turip.ui.compose.favorite.model.MoreOptionIcon
import com.on.turip.ui.compose.favorite.model.MoreOptionItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreOptionBottomSheet(
    screenMode: TuripPlaceScreenMode,
    onScreenModeChange: (TuripPlaceScreenMode) -> Unit,
    sheetState: SheetState,
    isDefault: Boolean,
    onDismiss: () -> Unit,
    onShareClick: () -> Unit,
    onInviteLinkClick: () -> Unit,
    onDeleteClick: () -> Unit,
    turipNameStatus: TuripNameStatusModel,
    turipName: String,
    onNameChanged: (name: String) -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    val items: List<MoreOptionItem> =
        listOf(
            MoreOptionItem(
                title = stringResource(R.string.turip_more_option_bottom_sheet_change_name),
                icon = MoreOptionIcon.Vector(Icons.Default.Create),
                color = TuripTheme.colors.black,
                onClick = { onScreenModeChange(TuripPlaceScreenMode.Edit) },
                enabled = !isDefault,
            ),
            MoreOptionItem(
                title = stringResource(R.string.turip_more_option_bottom_sheet_share_by_text),
                icon = MoreOptionIcon.Resource(R.drawable.ic_text_area),
                color = TuripTheme.colors.black,
                onClick = onShareClick,
            ),
            MoreOptionItem(
                title = stringResource(R.string.turip_more_option_bottom_sheet_invite_by_link),
                icon = MoreOptionIcon.Resource(R.drawable.ic_people_fill),
                color = TuripTheme.colors.black,
                onClick = onInviteLinkClick,
            ),
            MoreOptionItem(
                title = stringResource(R.string.turip_more_option_bottom_sheet_delete),
                icon = MoreOptionIcon.Vector(Icons.Default.Delete),
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
                        title = stringResource(R.string.bottom_sheet_turip_modify_title),
                        turipName = turipName,
                        onBack = { onScreenModeChange(TuripPlaceScreenMode.MoreOption) },
                        turipNameStatus = turipNameStatus,
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
        when (val icon: MoreOptionIcon = item.icon) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoreOptionBottomSheetPreviewContent(
    screenMode: TuripPlaceScreenMode,
    isDefault: Boolean,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var turipNameStatus by remember { mutableStateOf(TuripNameStatusModel.OK) }

    TuripTheme {
        MoreOptionBottomSheet(
            screenMode = screenMode,
            onScreenModeChange = {},
            sheetState = sheetState,
            isDefault = isDefault,
            onDismiss = {},
            onShareClick = {},
            onInviteLinkClick = {},
            onDeleteClick = {},
            turipNameStatus = turipNameStatus,
            turipName = "",
            onNameChanged = { newName ->
                turipNameStatus =
                    if (newName.isBlank()) TuripNameStatusModel.EMPTY else TuripNameStatusModel.OK
            },
            onConfirmClick = {},
        )
    }
}

@Preview(name = "MoreOption - 일반", showBackground = true)
@Composable
private fun MoreOptionBottomSheetPreviewMoreOption() {
    MoreOptionBottomSheetPreviewContent(
        screenMode = TuripPlaceScreenMode.MoreOption,
        isDefault = false,
    )
}

@Preview(name = "MoreOption - 기본폴더(삭제 비활성)", showBackground = true)
@Composable
private fun MoreOptionBottomSheetPreviewMoreOption_Default() {
    MoreOptionBottomSheetPreviewContent(
        screenMode = TuripPlaceScreenMode.MoreOption,
        isDefault = true,
    )
}

@Preview(name = "Edit", showBackground = true)
@Composable
private fun MoreOptionBottomSheetPreviewEdit() {
    MoreOptionBottomSheetPreviewContent(
        screenMode = TuripPlaceScreenMode.Edit,
        isDefault = false,
    )
}
