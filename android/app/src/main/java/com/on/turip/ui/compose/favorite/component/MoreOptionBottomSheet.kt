package com.on.turip.ui.compose.favorite.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreOptionBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TuripTheme.colors.white,
    ) {
        Column(modifier = modifier) {
            SettingItem(Icons.Default.Create, "이름 변경")
            HorizontalDivider(modifier = Modifier.padding(horizontal = TuripTheme.spacing.extraLarge))
            SettingItem(Icons.Default.AddCircle, "텍스트로 공유하기")
            HorizontalDivider(modifier = Modifier.padding(horizontal = TuripTheme.spacing.extraLarge))
            SettingItem(Icons.Default.Person, "링크로 초대하기")
            HorizontalDivider(modifier = Modifier.padding(horizontal = TuripTheme.spacing.extraLarge))
            SettingItem(Icons.Default.Delete, "삭제", color = TuripTheme.colors.error)
        }
    }
}

@Composable
private fun SettingItem(
    imageVector: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    color: Color = TuripTheme.colors.black,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = color,
            modifier =
                Modifier.padding(
                    horizontal = TuripTheme.spacing.extraHuge,
                    vertical = TuripTheme.spacing.large,
                ),
        )
        Text(
            text = text,
            style = TuripTheme.typography.title3,
            color = color,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
private fun MoreOptionBottomSheetPreview() {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    TuripTheme {
        MoreOptionBottomSheet(sheetState, {})
    }
}
