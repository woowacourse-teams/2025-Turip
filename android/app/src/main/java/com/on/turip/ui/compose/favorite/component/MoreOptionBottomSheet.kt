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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Immutable
private data class SheetItem(
    val title: String,
    val icon: SheetIcon,
    val color: Color,
    val onClick: () -> Unit,
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
    sheetState: SheetState,
    isDefault: Boolean,
    onDismiss: () -> Unit,
    onRenameClick: () -> Unit,
    onShareClick: () -> Unit,
    onInviteLinkClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val items: List<SheetItem> =
        listOf(
            SheetItem(
                title = "이름 변경",
                icon = SheetIcon.Vector(Icons.Default.Create),
                color = TuripTheme.colors.black,
                onClick = onRenameClick,
            ),
            SheetItem(
                title = "텍스트로 공유하기",
                icon = SheetIcon.Resource(R.drawable.ic_text_area),
                color = TuripTheme.colors.black,
                onClick = onShareClick,
            ),
            SheetItem(
                title = "링크로 초대하기",
                icon = SheetIcon.Resource(R.drawable.ic_people_fill),
                color = TuripTheme.colors.black,
                onClick = onInviteLinkClick,
            ),
            SheetItem(
                title = "삭제",
                icon = SheetIcon.Vector(Icons.Default.Delete),
                color = if (isDefault) TuripTheme.colors.errorContainer else TuripTheme.colors.error,
                onClick = if (isDefault) ({}) else onDeleteClick,
            ),
        )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TuripTheme.colors.white,
    ) {
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
                .clickable(onClick = item.onClick)
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
@Preview(showBackground = true)
private fun MoreOptionBottomSheetPreview() {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    TuripTheme {
        MoreOptionBottomSheet(sheetState, false, {}, {}, {}, {}, {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
private fun MoreOptionDefaultBottomSheetPreview() {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    TuripTheme {
        MoreOptionBottomSheet(sheetState, true, {}, {}, {}, {}, {})
    }
}
