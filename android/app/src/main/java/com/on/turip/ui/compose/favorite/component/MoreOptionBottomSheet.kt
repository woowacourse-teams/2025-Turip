package com.on.turip.ui.compose.favorite.component

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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.favorite.TuripPlaceScreenMode
import com.on.turip.ui.compose.folder.component.FolderBottomSheetContent
import com.on.turip.ui.folder.model.TuripNameStatusModel

@Immutable
private data class SheetItem(
    val title: String,
    val icon: SheetIcon,
    val color: Color,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
)

@Immutable
private sealed interface SheetIcon {
    data class Vector(
        val imageVector: ImageVector,
    ) : SheetIcon

    data class Resource(
        val resId: Int,
    ) : SheetIcon
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreOptionBottomSheet(
    screenMode: TuripPlaceScreenMode,
    sheetState: SheetState,
    isDefault: Boolean,
    onDismiss: () -> Unit,
    onRenameClick: () -> Unit,
    onShareClick: () -> Unit,
    onInviteLinkClick: () -> Unit,
    onDeleteClick: () -> Unit,
    turipNameStatus: TuripNameStatusModel,
    onNameChanged: (String) -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    var folderName by remember { mutableStateOf("") }

    val items: List<SheetItem> =
        listOf(
            SheetItem(
                title = stringResource(R.string.turip_more_option_bottom_sheet_change_name),
                icon = SheetIcon.Vector(Icons.Default.Create),
                color = TuripTheme.colors.black,
                onClick = onRenameClick,
                enabled = !isDefault,
            ),
            SheetItem(
                title = stringResource(R.string.turip_more_option_bottom_sheet_share_by_text),
                icon = SheetIcon.Resource(R.drawable.ic_text_area),
                color = TuripTheme.colors.black,
                onClick = onShareClick,
            ),
            SheetItem(
                title = stringResource(R.string.turip_more_option_bottom_sheet_invite_by_link),
                icon = SheetIcon.Resource(R.drawable.ic_people_fill),
                color = TuripTheme.colors.black,
                onClick = onInviteLinkClick,
            ),
            SheetItem(
                title = stringResource(R.string.turip_more_option_bottom_sheet_delete),
                icon = SheetIcon.Vector(Icons.Default.Delete),
                color = TuripTheme.colors.error,
                onClick = onDeleteClick,
                enabled = !isDefault,
            ),
        )
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TuripTheme.colors.white,
    ) {
        when (screenMode) {
            TuripPlaceScreenMode.MoreOption -> {
                Column(modifier = modifier) {
                    items.forEachIndexed { index, item ->
                        SheetSettingItem(item = item)

                        if (index != items.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = TuripTheme.spacing.extraLarge),
                            )
                        }
                    }
                }
            }

            TuripPlaceScreenMode.Edit -> {
                FolderBottomSheetContent(
                    title = stringResource(R.string.bottom_sheet_turip_modify_title),
                    folderName = folderName,
                    onFolderNameChange = {
                        folderName = it
                    },
                    turipNameStatus = turipNameStatus,
                    onNameChanged = onNameChanged,
                    onConfirmClick = onConfirmClick,
                    focusRequester = focusRequester,
                )
            }
        }
    }
}

@Composable
private fun SheetSettingItem(
    item: SheetItem,
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
        when (val icon: SheetIcon = item.icon) {
            is SheetIcon.Vector -> {
                Icon(
                    imageVector = icon.imageVector,
                    contentDescription = null,
                    tint = item.color,
                    modifier = Modifier.size(18.dp),
                )
            }

            is SheetIcon.Resource -> {
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

    var name by remember { mutableStateOf("내 여행 폴더") }
    var turipNameStatus by remember { mutableStateOf(TuripNameStatusModel.OK) }

    TuripTheme {
        MoreOptionBottomSheet(
            screenMode = screenMode,
            sheetState = sheetState,
            isDefault = isDefault,
            onDismiss = {},
            onRenameClick = {},
            onShareClick = {},
            onInviteLinkClick = {},
            onDeleteClick = {},
            turipNameStatus = turipNameStatus,
            onNameChanged = { newName ->
                name = newName
                turipNameStatus =
                    if (newName.isBlank()) TuripNameStatusModel.EMPTY else TuripNameStatusModel.OK
            },
            onConfirmClick = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "MoreOption - 일반", showBackground = true)
@Composable
private fun MoreOptionBottomSheetPreview_MoreOption() {
    MoreOptionBottomSheetPreviewContent(
        screenMode = TuripPlaceScreenMode.MoreOption,
        isDefault = false,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "MoreOption - 기본폴더(삭제 비활성)", showBackground = true)
@Composable
private fun MoreOptionBottomSheetPreview_MoreOption_Default() {
    MoreOptionBottomSheetPreviewContent(
        screenMode = TuripPlaceScreenMode.MoreOption,
        isDefault = true,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Edit", showBackground = true)
@Composable
private fun MoreOptionBottomSheetPreview_Edit() {
    MoreOptionBottomSheetPreviewContent(
        screenMode = TuripPlaceScreenMode.Edit,
        isDefault = false,
    )
}
