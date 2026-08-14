package com.on.turip.core.ui.component

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldDecorator
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_confirm
import com.on.turip.core.designsystem.generated.resources.bottom_sheet_turip_add_turip_name_hint
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.model.namestatus.TuripNameStatusModel
import kotlinx.coroutines.flow.drop
import org.jetbrains.compose.resources.stringResource

/**
 * 튜립명 입력 바텀시트 본문.
 *
 * 입력 텍스트의 소유자는 이 컴포저블이 가진 [androidx.compose.foundation.text.input.TextFieldState] 다.
 * iOS 한글 IME 는 조합 중인 글자를 marked text 로 관리하는데, 입력값을 ViewModel 의 StateFlow 로
 * 왕복시키면 조합 도중 한 프레임 뒤늦게 값이 되돌아와 조합이 끊어진다.
 * 그래서 [initialTuripName] 은 최초 구성 시 초기값으로만 쓰이고, 이후에는 [onNameChanged] 로
 * 단방향 통지만 한다. 외부에서 텍스트를 갱신하려면 이 컴포저블을 다시 구성해야 한다.
 *
 * [maxLength] 는 입력 버퍼에 글자를 더 넣을지 말지만 정한다. 이름이 유효한지에 대한 판정은
 * 도메인(`TuripNameStatus`)이 하고, 그 결과가 [turipNameStatus] 로 들어온다.
 */
@Composable
fun NameEditorSheetContent(
    title: String,
    initialTuripName: String,
    maxLength: Int,
    turipNameStatus: TuripNameStatusModel,
    isConfirmEnabled: Boolean,
    onNameChanged: (turipName: String) -> Unit,
    onConfirmClick: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
) {
    val textFieldState = rememberTextFieldState(initialText = initialTuripName)
    val currentOnNameChanged by rememberUpdatedState(onNameChanged)

    LaunchedEffect(Unit) {
        withFrameNanos { }
        focusRequester.requestFocus()
    }

    LaunchedEffect(textFieldState) {
        snapshotFlow { textFieldState.text.toString() }
            .drop(1)
            .collect { name -> currentOnNameChanged(name) }
    }

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
            state = textFieldState,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp)
                    .focusRequester(focusRequester),
            textStyle = TuripTheme.typography.title3.copy(color = TuripTheme.colors.black),
            lineLimits = TextFieldLineLimits.SingleLine,
            // 길이 제한은 ViewModel 에서 입력을 되돌리지 않고 여기서 처리한다.
            // 되돌리면 iOS 에서 IME 의 marked text 와 상태가 어긋나 한글 조합이 깨진다.
            inputTransformation = InputTransformation.maxLength(maxLength),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            onKeyboardAction = KeyboardActionHandler { if (isConfirmEnabled) onConfirmClick() },
            decorator = TextFieldDecorator { innerTextField ->
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
                            ).padding(
                                horizontal = TuripTheme.spacing.medium,
                                vertical = TuripTheme.spacing.small,
                            ),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (textFieldState.text.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.bottom_sheet_turip_add_turip_name_hint),
                            color = TuripTheme.colors.gray02,
                            style = TuripTheme.typography.title3.copy(fontWeight = FontWeight.Normal),
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
            enabled = isConfirmEnabled,
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
                text = stringResource(Res.string.all_confirm),
                style = TuripTheme.typography.title3,
                color = TuripTheme.colors.white,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun NameEditorSheetContentPreview() {
    val focusRequester: FocusRequester = remember { FocusRequester() }

    TuripTheme {
        NameEditorSheetContent(
            title = "튜립 수정",
            turipNameStatus = TuripNameStatusModel.OK,
            onNameChanged = {},
            onConfirmClick = {},
            onBack = {},
            initialTuripName = "",
            maxLength = 20,
            isConfirmEnabled = true,
            focusRequester = focusRequester,
        )
    }
}
