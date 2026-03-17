package com.on.turip.ui.compose.turipdetail

import androidx.compose.runtime.Immutable
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.model.namestatus.TuripNameStatusModel
import com.on.turip.ui.compose.trip.turipselection.model.TuripPlaceModel
import com.on.turip.ui.compose.turip.model.MyTuripModel
import com.on.turip.ui.compose.turipdetail.model.turip.PlaceLatLngUiModel
import com.on.turip.ui.folder.model.TuripEditModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class TuripDetailUiState(
    val isLoading: Boolean,
    val inputTuripName: String,
    val errorUiState: ErrorUiState,
    val showBottomSheet: Boolean,
    val showMemberBottomSheet: Boolean,
    val members: ImmutableList<String>,
    val turipNameStatus: TuripNameStatusModel,
    val screenMode: TuripPlaceScreenMode,
    val editModels: ImmutableList<TuripEditModel>,
    val selectedTurip: MyTuripModel,
    val selectedPlace: PlaceLatLngUiModel,
    val showTuripRemoveDialog: Boolean,
    val places: ImmutableList<TuripPlaceModel>,
    val placesLatLng: ImmutableList<PlaceLatLngUiModel>,
) {
    companion object {
        val Idle: TuripDetailUiState =
            TuripDetailUiState(
                isLoading = true,
                inputTuripName = "",
                errorUiState = ErrorUiState.None,
                showMemberBottomSheet = false,
                members = persistentListOf(),
                showBottomSheet = false,
                turipNameStatus = TuripNameStatusModel.EMPTY,
                selectedTurip = MyTuripModel.Idle,
                places = persistentListOf(),
                placesLatLng = persistentListOf(),
                editModels = persistentListOf(),
                screenMode = TuripPlaceScreenMode.MoreOption,
                showTuripRemoveDialog = false,
                selectedPlace = PlaceLatLngUiModel.Idle,
            )
    }
}

@Immutable
sealed interface TuripPlaceScreenMode {
    data object MoreOption : TuripPlaceScreenMode

    data object Edit : TuripPlaceScreenMode
}
