package com.on.turip.ui.compose.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import com.on.turip.ui.compose.designsystem.model.TuripNameStatusModel
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun NameEditorSheetContent(
    title: String,
    turipName: String,
    turipNameStatus: TuripNameStatusModel,
    onNameChanged: (turipName: String) -> Unit,
    onConfirmClick: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(bottom = TuripTheme.spacing.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        top = TuripTheme.spacing.medium,
                        bottom = TuripTheme.spacing.huge,
                    ),
        ) {
            onBack?.let {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                    tint = TuripTheme.colors.gray03,
                    modifier =
                        Modifier
                            .padding(start = TuripTheme.spacing.medium)
                            .clickable { onBack() },
                )
            }

            Text(
                text = title,
                style = TuripTheme.typography.title2,
                color = TuripTheme.colors.black,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        BasicTextField(
            value = turipName,
            onValueChange = { input -> onNameChanged(input) },
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
                            )
                            .background(
                                color = TuripTheme.colors.white,
                                shape = TuripTheme.shape.container,
                            )
                            .padding(horizontal = TuripTheme.spacing.medium, vertical = TuripTheme.spacing.small),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (turipName.isEmpty()) {
                        Text(
                            text = stringResource(R.string.bottom_sheet_turip_add_turip_name_hint),
                            color = TuripTheme.colors.gray02,
                            style = TuripTheme.typography.title3,
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
fun NameEditorSheetContentPreview(
    @PreviewParameter(TuripNameStatusPreviewProvider::class)
    status: TuripNameStatusModel,
) {
    val focusRequester: FocusRequester = remember { FocusRequester() }

    TuripTheme {
        NameEditorSheetContent(
            title = "튜립 수정",
            turipNameStatus = status,
            onNameChanged = {},
            onConfirmClick = {},
            turipName = "",
            focusRequester = focusRequester,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun NameEditorSheetContentPreview() {
    val focusRequester: FocusRequester = remember { FocusRequester() }

    TuripTheme {
        NameEditorSheetContent(
            title = "튜립 수정",
            turipNameStatus = TuripNameStatusModel.OK,
            onNameChanged = {},
            onConfirmClick = {},
            onBack = {},
            turipName = "",
            focusRequester = focusRequester,
        )
    }
}
