package com.on.turip.feature.turipdetail.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.component.TuripAppBar
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.*
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.turipdetail.impl.model.MapModel
import com.on.turip.feature.turipdetail.impl.component.MoreOptionBottomSheet
import com.on.turip.feature.turipdetail.impl.component.TuripInfoRow
import com.on.turip.feature.turipdetail.impl.component.TuripMapContent
import com.on.turip.feature.turipdetail.impl.component.TuripPlaces
import com.on.turip.feature.turipdetail.impl.model.TuripNameStatusModel
import com.on.turip.feature.turipdetail.impl.model.TuripPlaceModel
import com.on.turip.feature.turipdetail.impl.model.turip.PlaceLatLngUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TuripDetailScreen(
    selectedTuripId: Long,
    onNavigateToLogin: () -> Unit,
    onNavigateToMap: (map: MapModel) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showMoreOptionBottomSheet by remember { mutableStateOf(false) }
    var screenMode by remember { mutableStateOf(TuripPlaceScreenMode.MoreOption) }
    var selectedTuripName by remember { mutableStateOf("서울 여행") }
    var turipNameStatus by remember { mutableStateOf(TuripNameStatusModel.OK) }

    if (showMoreOptionBottomSheet) {
        MoreOptionBottomSheet(
            sheetState = modalBottomSheetState,
            isDefault = false,
            isTogetherTurip = true,
            onDismiss = {
                showMoreOptionBottomSheet = false
                screenMode = TuripPlaceScreenMode.MoreOption
            },
            onShareTuripByTextClick = {},
            onShareTuripInvitationLinkClick = {},
            onDeleteClick = {},
            screenMode = screenMode,
            onScreenModeChange = { screenMode = it },
            turipNameStatus = turipNameStatus,
            turipName = selectedTuripName,
            onNameChanged = { input ->
                selectedTuripName = input
                turipNameStatus = if (input.isBlank()) TuripNameStatusModel.EMPTY else TuripNameStatusModel.OK
            },
            onConfirmClick = {
                showMoreOptionBottomSheet = false
                screenMode = TuripPlaceScreenMode.MoreOption
            },
        )
    }

    TuripPlaceContent(
        nicknames = persistentListOf("여행자", "튜립"),
        isTogetherTurip = true,
        selectedTuripId = selectedTuripId,
        selectedTuripName = selectedTuripName,
        selectedPlace = samplePlaceLatLng.first(),
        currentPlaceLatLng = samplePlaceLatLng,
        turipPlaceModel = sampleTuripPlaces,
        navigateToMap = onNavigateToMap,
        onClickTuripPlace = {},
        onClickMembers = {},
        onMoreOption = { showMoreOptionBottomSheet = true },
        onItemClick = {},
        onDragStart = {},
        onDragPlace = { _, _ -> },
        onDragEnd = {},
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
private fun TuripPlaceContent(
    nicknames: ImmutableList<String>,
    isTogetherTurip: Boolean,
    selectedTuripId: Long,
    selectedTuripName: String,
    selectedPlace: PlaceLatLngUiModel,
    onItemClick: (placeId: Long) -> Unit,
    currentPlaceLatLng: ImmutableList<PlaceLatLngUiModel>,
    turipPlaceModel: ImmutableList<TuripPlaceModel>,
    navigateToMap: (map: MapModel) -> Unit,
    onClickTuripPlace: (placeId: Long) -> Unit,
    onClickMembers: () -> Unit,
    onMoreOption: () -> Unit,
    onDragStart: () -> Unit,
    onDragPlace: (from: Int, to: Int) -> Unit,
    onDragEnd: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isMapVisible by remember { mutableStateOf(true) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .systemBarsPadding()
                .background(TuripTheme.colors.white),
    ) {
        Header(
            turipName = selectedTuripName,
            onBackClick = onBack,
            onMoreOption = onMoreOption,
        )

        TuripInfoRow(
            savedPlaceCount = currentPlaceLatLng.size,
            participantCount = nicknames.size,
            showParticipant = isTogetherTurip,
            onMembersClick = onClickMembers,
            onPlacesClick = { isMapVisible = false },
            modifier = Modifier.padding(vertical = TuripTheme.spacing.small),
        )

        Spacer(modifier = Modifier.size(TuripTheme.spacing.extraSmall))

        if (currentPlaceLatLng.isNotEmpty()) {
            TuripMapContent(
                selectedTuripId = selectedTuripId,
                selectedPlace = selectedPlace,
                places = currentPlaceLatLng,
                isMapVisible = isMapVisible,
                onMapToggle = { isMapVisible = !isMapVisible },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        TuripPlaces(
            places = turipPlaceModel,
            onItemClick = onItemClick,
            onMapClick = navigateToMap,
            onTuripPlaceClick = onClickTuripPlace,
            onDragStart = onDragStart,
            onDragPlace = onDragPlace,
            onDragEnd = onDragEnd,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun Header(
    turipName: String,
    onBackClick: () -> Unit,
    onMoreOption: () -> Unit,
) {
    TuripAppBar(
        start = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(Res.string.all_back_description),
                    tint = TuripTheme.colors.gray03,
                )
            }
        },
        center = {
            Text(
                text = turipName,
                style = TuripTheme.typography.title1,
                color = TuripTheme.colors.black,
            )
        },
        end = {
            IconButton(onClick = onMoreOption) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = TuripTheme.colors.gray03,
                )
            }
        },
    )
}

private val samplePlaceLatLng =
    persistentListOf(
        PlaceLatLngUiModel(1L, "안국역", 37.576, 126.985),
        PlaceLatLngUiModel(2L, "북촌한옥마을", 37.582, 126.983),
    )

private val sampleTuripPlaces =
    persistentListOf(
        TuripPlaceModel.Idle.copy(
            turipPlaceId = 1L,
            placeId = 1L,
            name = "안국역",
            category = "역",
            isTuripPlace = true,
        ),
        TuripPlaceModel.Idle.copy(
            turipPlaceId = 2L,
            placeId = 2L,
            name = "북촌한옥마을",
            category = "명소",
            isTuripPlace = true,
        ),
    )

@Preview(showBackground = true)
@Composable
private fun TuripDetailScreenPreview() {
    TuripTheme {
        TuripDetailScreen(
            selectedTuripId = 1L,
            onNavigateToLogin = {},
            onNavigateToMap = {},
            onBack = {},
        )
    }
}
