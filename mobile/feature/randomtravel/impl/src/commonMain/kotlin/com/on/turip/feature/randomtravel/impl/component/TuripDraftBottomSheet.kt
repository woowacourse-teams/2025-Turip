package com.on.turip.feature.randomtravel.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldDecorator
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.component.TuripLoadingIndicator
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.bottom_sheet_turip_add_turip_name_hint
import com.on.turip.core.designsystem.generated.resources.btn_turip_place_normal
import com.on.turip.core.designsystem.generated.resources.btn_turip_place_selected
import com.on.turip.core.designsystem.generated.resources.random_travel_turip_draft_clear_all
import com.on.turip.core.designsystem.generated.resources.random_travel_turip_draft_confirm
import com.on.turip.core.designsystem.generated.resources.random_travel_turip_draft_place_section
import com.on.turip.core.designsystem.generated.resources.random_travel_turip_draft_places_error
import com.on.turip.core.designsystem.generated.resources.random_travel_turip_draft_retry
import com.on.turip.core.designsystem.generated.resources.random_travel_turip_draft_select_all
import com.on.turip.core.designsystem.generated.resources.random_travel_turip_draft_skip
import com.on.turip.core.designsystem.generated.resources.random_travel_turip_draft_title
import com.on.turip.core.designsystem.generated.resources.trip_detail_bottom_sheet_turip_place_select_description
import com.on.turip.core.designsystem.generated.resources.trip_detail_bottom_sheet_turip_place_unselect_description
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.model.turip.TuripNameStatus
import com.on.turip.feature.randomtravel.impl.RandomTravelIntent
import com.on.turip.feature.randomtravel.impl.TuripDraftUiState
import com.on.turip.feature.randomtravel.impl.model.TuripDraftPlaceModel
import kotlinx.coroutines.flow.drop
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val SHEET_HORIZONTAL_PADDING = 20.dp
private val PLACE_LIST_MAX_HEIGHT = 260.dp
private val NAME_FIELD_HEIGHT = 40.dp
private val TOGGLE_ICON_SIZE = 24.dp

/**
 * `이 여행 시작하기`를 누르면 뜨는 확인 시트.
 *
 * 예전에는 곧장 영상으로 이동했는데, 지금은 영상의 장소를 담을 튜립을 여기서 한 번 확인받는다.
 * 담지 않고 영상만 보는 길([RandomTravelIntent.SkipTuripDraft])을 항상 함께 열어 둔다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TuripDraftBottomSheet(
    turipDraftUiState: TuripDraftUiState,
    onIntent: (RandomTravelIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (turipDraftUiState == TuripDraftUiState.Hidden) return

    val isSaving: Boolean = (turipDraftUiState as? TuripDraftUiState.Ready)?.isSaving == true
    // 담는 중에 시트가 닫히면 만들어진 튜립의 결과를 알릴 곳이 없어진다.
    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { sheetValue: SheetValue ->
                sheetValue != SheetValue.Hidden || !isSaving
            },
        )

    ModalBottomSheet(
        onDismissRequest = { onIntent(RandomTravelIntent.DismissTuripDraft) },
        sheetState = sheetState,
        containerColor = TuripTheme.colors.white,
        shape = TuripTheme.shape.bottomSheetRounded,
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(bottom = TuripTheme.spacing.extraLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(Res.string.random_travel_turip_draft_title),
                style = TuripTheme.typography.title2,
                color = TuripTheme.colors.black,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            top = TuripTheme.spacing.medium,
                            bottom = TuripTheme.spacing.large,
                        ),
            )

            when (turipDraftUiState) {
                TuripDraftUiState.Hidden -> Unit

                TuripDraftUiState.Loading ->
                    Box(
                        modifier = Modifier.fillMaxWidth().height(PLACE_LIST_MAX_HEIGHT),
                        contentAlignment = Alignment.Center,
                    ) {
                        TuripLoadingIndicator()
                    }

                TuripDraftUiState.Error ->
                    TuripDraftErrorContent(onIntent = onIntent)

                is TuripDraftUiState.Ready ->
                    TuripDraftReadyContent(
                        turipDraftUiState = turipDraftUiState,
                        onIntent = onIntent,
                    )
            }
        }
    }
}

@Composable
private fun TuripDraftErrorContent(
    onIntent: (RandomTravelIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = SHEET_HORIZONTAL_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.large),
    ) {
        Text(
            text = stringResource(Res.string.random_travel_turip_draft_places_error),
            style = TuripTheme.typography.body1,
            color = TuripTheme.colors.gray03,
        )
        TuripDraftPrimaryButton(
            text = stringResource(Res.string.random_travel_turip_draft_retry),
            enabled = true,
            isLoading = false,
            onClick = { onIntent(RandomTravelIntent.RetryTuripDraftPlaces) },
        )
        TuripDraftSkipButton(onIntent = onIntent)
    }
}

@Composable
private fun TuripDraftReadyContent(
    turipDraftUiState: TuripDraftUiState.Ready,
    onIntent: (RandomTravelIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        TuripDraftNameField(
            initialName = turipDraftUiState.name,
            errorMessage = turipDraftUiState.nameStatus.errorMessage?.let { stringResource(it) },
            isConfirmEnabled = turipDraftUiState.canConfirm,
            onNameChanged = { name -> onIntent(RandomTravelIntent.ChangeTuripDraftName(name)) },
            onConfirmClick = { onIntent(RandomTravelIntent.ConfirmTuripDraft) },
        )

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SHEET_HORIZONTAL_PADDING)
                    .padding(top = TuripTheme.spacing.large),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text =
                    stringResource(
                        Res.string.random_travel_turip_draft_place_section,
                        turipDraftUiState.selectedPlaceIds.size,
                        turipDraftUiState.places.size,
                    ),
                style = TuripTheme.typography.info1,
                color = TuripTheme.colors.gray03,
                modifier = Modifier.weight(1f),
            )
            Text(
                text =
                    stringResource(
                        if (turipDraftUiState.isAllSelected) {
                            Res.string.random_travel_turip_draft_clear_all
                        } else {
                            Res.string.random_travel_turip_draft_select_all
                        },
                    ),
                style = TuripTheme.typography.info1,
                color = TuripTheme.colors.primary,
                modifier =
                    Modifier
                        .clip(TuripTheme.shape.container)
                        .clickable(enabled = !turipDraftUiState.isSaving) {
                            onIntent(RandomTravelIntent.ToggleTuripDraftAllPlaces)
                        }.padding(TuripTheme.spacing.extraSmall),
            )
        }

        LazyColumn(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = PLACE_LIST_MAX_HEIGHT)
                    .padding(top = TuripTheme.spacing.small),
        ) {
            items(
                items = turipDraftUiState.places,
                key = { place: TuripDraftPlaceModel -> place.placeId },
            ) { place: TuripDraftPlaceModel ->
                TuripDraftPlaceItem(
                    place = place,
                    enabled = !turipDraftUiState.isSaving,
                    onClick = { onIntent(RandomTravelIntent.ToggleTuripDraftPlace(place.placeId)) },
                )
            }
        }

        Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

        TuripDraftPrimaryButton(
            text = stringResource(Res.string.random_travel_turip_draft_confirm),
            enabled = turipDraftUiState.canConfirm,
            isLoading = turipDraftUiState.isSaving,
            onClick = { onIntent(RandomTravelIntent.ConfirmTuripDraft) },
            modifier = Modifier.padding(horizontal = SHEET_HORIZONTAL_PADDING),
        )

        if (!turipDraftUiState.isSaving) {
            TuripDraftSkipButton(
                onIntent = onIntent,
                modifier = Modifier.padding(horizontal = SHEET_HORIZONTAL_PADDING),
            )
        } else {
            Spacer(modifier = Modifier.height(TuripTheme.spacing.extraExtraLarge))
        }
    }
}

/**
 * 입력 텍스트의 소유자는 이 컴포저블의 `TextFieldState` 다.
 * ViewModel 로 값을 왕복시키면 iOS 한글 IME 의 조합이 끊기기 때문에 [initialName] 은 최초 구성 시에만 쓰고
 * 이후에는 [onNameChanged] 로 단방향 통지만 한다.
 */
@Composable
private fun TuripDraftNameField(
    initialName: String,
    errorMessage: String?,
    isConfirmEnabled: Boolean,
    onNameChanged: (name: String) -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val textFieldState = rememberTextFieldState(initialText = initialName)
    val currentOnNameChanged by rememberUpdatedState(onNameChanged)

    LaunchedEffect(textFieldState) {
        snapshotFlow { textFieldState.text.toString() }
            .drop(1)
            .collect { name -> currentOnNameChanged(name) }
    }

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = SHEET_HORIZONTAL_PADDING)) {
        BasicTextField(
            state = textFieldState,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TuripTheme.typography.title3.copy(color = TuripTheme.colors.black),
            lineLimits = TextFieldLineLimits.SingleLine,
            // 길이 제한을 ViewModel 에서 되돌리면 iOS 에서 IME 의 marked text 와 어긋나 조합이 깨진다.
            inputTransformation = InputTransformation.maxLength(TuripNameStatus.MAX_LENGTH),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            onKeyboardAction = KeyboardActionHandler { if (isConfirmEnabled) onConfirmClick() },
            decorator = TextFieldDecorator { innerTextField ->
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(NAME_FIELD_HEIGHT)
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

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = TuripTheme.colors.error,
                style = TuripTheme.typography.info2,
                modifier = Modifier.padding(top = TuripTheme.spacing.extraSmall),
            )
        }
    }
}

@Composable
private fun TuripDraftPlaceItem(
    place: TuripDraftPlaceModel,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(enabled = enabled, onClick = onClick)
                .padding(
                    horizontal = SHEET_HORIZONTAL_PADDING,
                    vertical = TuripTheme.spacing.small,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = place.name,
                style = TuripTheme.typography.body1,
                color = TuripTheme.colors.black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (place.category.isNotBlank()) {
                Text(
                    text = place.category,
                    style = TuripTheme.typography.info2,
                    color = TuripTheme.colors.gray03,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Icon(
            painter =
                painterResource(
                    if (place.isSelected) {
                        Res.drawable.btn_turip_place_selected
                    } else {
                        Res.drawable.btn_turip_place_normal
                    },
                ),
            contentDescription =
                stringResource(
                    if (place.isSelected) {
                        Res.string.trip_detail_bottom_sheet_turip_place_unselect_description
                    } else {
                        Res.string.trip_detail_bottom_sheet_turip_place_select_description
                    },
                ),
            tint = Color.Unspecified,
            modifier = Modifier.size(TOGGLE_ICON_SIZE),
        )
    }
}

@Composable
private fun TuripDraftPrimaryButton(
    text: String,
    enabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = TuripTheme.shape.wideButton,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = TuripTheme.colors.primary,
                disabledContainerColor = TuripTheme.colors.gray02,
            ),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = TuripTheme.spacing.small),
    ) {
        if (isLoading) {
            TuripLoadingIndicator(
                size = TOGGLE_ICON_SIZE,
                color = TuripTheme.colors.white,
            )
        } else {
            Text(
                text = text,
                style = TuripTheme.typography.title3,
                color = TuripTheme.colors.white,
            )
        }
    }
}

@Composable
private fun TuripDraftSkipButton(
    onIntent: (RandomTravelIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = { onIntent(RandomTravelIntent.SkipTuripDraft) },
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(Res.string.random_travel_turip_draft_skip),
            style = TuripTheme.typography.body1,
            color = TuripTheme.colors.gray03,
        )
    }
}
