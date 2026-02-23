package com.on.turip.ui.compose.folder.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.folder.model.TuripNameStatusModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderAddBottomSheet(
    sheetState: SheetState,
    turipNameStatus: TuripNameStatusModel,
    onNameChanged: (String) -> Unit,
    onConfirmClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    var folderName by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TuripTheme.colors.white,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = TuripTheme.spacing.extraLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.bottom_sheet_turip_add_title),
                style = TuripTheme.typography.title2,
                color = TuripTheme.colors.black,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = TuripTheme.spacing.medium, bottom = TuripTheme.spacing.huge),
            )

            BasicTextField(
                value = folderName,
                onValueChange = { input ->
                    if (input.length <= 20) {
                        folderName = input
                        onNameChanged(input)
                    }
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 36.dp)
                        .focusRequester(focusRequester),
                textStyle = TuripTheme.typography.title3.copy(color = TuripTheme.colors.black),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions =
                    KeyboardActions(
                        onDone = { if (turipNameStatus.isConfirmEnabled) onConfirmClick() },
                    ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .clip(TuripTheme.shape.container)
                                .border(
                                    width = 1.dp,
                                    color = TuripTheme.colors.gray04,
                                    shape = TuripTheme.shape.container,
                                ).background(
                                    color = TuripTheme.colors.white,
                                    shape = TuripTheme.shape.container,
                                ).padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (folderName.isEmpty()) {
                            Text(
                                text = stringResource(R.string.bottom_sheet_turip_add_turip_name_hint),
                                color = TuripTheme.colors.gray02,
                                style = TuripTheme.typography.body2,
                            )
                        }
                        innerTextField()
                    }
                },
            )

            if (turipNameStatus.errorMessage != null) {
                Text(
                    text = stringResource(turipNameStatus.errorMessage),
                    color = TuripTheme.colors.error,
                    style = TuripTheme.typography.info2,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 40.dp, top = TuripTheme.spacing.small),
                )
            } else {
                Spacer(modifier = Modifier.height(TuripTheme.spacing.small))
            }

            Button(
                onClick = onConfirmClick,
                enabled = turipNameStatus.isConfirmEnabled,
                shape = TuripTheme.shape.wideButton,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = TuripTheme.colors.primary,
                        disabledContainerColor = TuripTheme.colors.gray02,
                    ),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 36.dp)
                        .padding(top = TuripTheme.spacing.extraSmall),
                contentPadding = PaddingValues(vertical = TuripTheme.spacing.small),
            ) {
                Text(
                    text = stringResource(R.string.all_confirm),
                    style = TuripTheme.typography.title3,
                    color = TuripTheme.colors.white,
                )
            }
        }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
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
fun FolderAddBottomSheetStatusPreview(
    @PreviewParameter(TuripNameStatusPreviewProvider::class)
    status: TuripNameStatusModel,
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    TuripTheme {
        FolderAddBottomSheet(
            sheetState = sheetState,
            turipNameStatus = status,
            onNameChanged = {},
            onConfirmClick = {},
            onDismiss = {},
        )
    }
}
