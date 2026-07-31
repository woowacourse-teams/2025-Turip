package com.on.turip.feature.turip.impl.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.core.designsystem.component.TuripSnackbar
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.component.NameEditorSheetContent
import com.on.turip.core.ui.model.namestatus.TuripNameStatusModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TuripAddBottomSheet(
    title: String,
    initialTuripName: String,
    sheetState: SheetState,
    turipNameStatus: TuripNameStatusModel,
    isConfirmEnabled: Boolean,
    onNameChanged: (name: String) -> Unit,
    onConfirmClick: () -> Unit,
    onDismiss: () -> Unit,
    snackbarHostState: SnackbarHostState? = null,
) {
    val focusRequester = remember { FocusRequester() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TuripTheme.colors.white,
    ) {
        if (snackbarHostState != null) {
            Box(modifier = Modifier.imePadding()) {
                NameEditorSheetContent(
                    title = title,
                    initialTuripName = initialTuripName,
                    turipNameStatus = turipNameStatus,
                    isConfirmEnabled = isConfirmEnabled,
                    onNameChanged = onNameChanged,
                    onConfirmClick = onConfirmClick,
                    focusRequester = focusRequester,
                )
                TuripSnackbar(
                    snackbarHostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                )
            }
        } else {
            NameEditorSheetContent(
                title = title,
                initialTuripName = initialTuripName,
                turipNameStatus = turipNameStatus,
                isConfirmEnabled = isConfirmEnabled,
                onNameChanged = onNameChanged,
                onConfirmClick = onConfirmClick,
                focusRequester = focusRequester,
                modifier = Modifier.imePadding(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    name = "FolderAddBottomSheet - Status Variants",
    showBackground = true,
)
@Composable
private fun TuripAddBottomSheetStatusPreview() {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }

    TuripTheme {
        TuripAddBottomSheet(
            title = "튜립 추가",
            initialTuripName = "",
            sheetState = sheetState,
            turipNameStatus = TuripNameStatusModel.OK,
            isConfirmEnabled = true,
            onNameChanged = {},
            onConfirmClick = {},
            onDismiss = {},
            snackbarHostState = snackbarHostState,
        )
    }
}
