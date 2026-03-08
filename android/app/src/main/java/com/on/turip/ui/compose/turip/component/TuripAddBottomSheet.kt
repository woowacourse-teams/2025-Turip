package com.on.turip.ui.compose.turip.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.on.turip.ui.common.component.NameEditorSheetContent
import com.on.turip.ui.common.model.namestatus.TuripNameStatusModel
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TuripAddBottomSheet(
    title: String,
    turipName: String,
    sheetState: SheetState,
    turipNameStatus: TuripNameStatusModel,
    isConfirmEnabled: Boolean,
    onNameChanged: (name: String) -> Unit,
    onConfirmClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TuripTheme.colors.white,
    ) {
        Box {
            NameEditorSheetContent(
                title = title,
                turipName = turipName,
                turipNameStatus = turipNameStatus,
                isConfirmEnabled = isConfirmEnabled,
                onNameChanged = onNameChanged,
                onConfirmClick = onConfirmClick,
                focusRequester = focusRequester,
            )
        }
    }
}

class TuripNameStatusPreviewProvider : PreviewParameterProvider<TuripNameStatusModel> {
    override val values: Sequence<TuripNameStatusModel> =
        sequenceOf(
            TuripNameStatusModel.OK,
            TuripNameStatusModel.EMPTY,
            TuripNameStatusModel.DUPLICATE_NAME,
            TuripNameStatusModel.DEFAULT_TURIP_NAME,
            TuripNameStatusModel.MAX_LENGTH_TURIP_NAME,
            TuripNameStatusModel.OUT_OF_BOUND_LENGTH,
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    name = "FolderAddBottomSheet - Status Variants",
    showBackground = true,
)
@Composable
fun TuripAddBottomSheetStatusPreview(
    @PreviewParameter(TuripNameStatusPreviewProvider::class)
    status: TuripNameStatusModel,
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    TuripTheme {
        TuripAddBottomSheet(
            title = "튜립 추가",
            turipName = "",
            sheetState = sheetState,
            turipNameStatus = status,
            isConfirmEnabled = true,
            onNameChanged = {},
            onConfirmClick = {},
            onDismiss = {},
        )
    }
}
