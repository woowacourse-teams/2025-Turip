package com.on.turip.ui.compose.favorite

import androidx.compose.runtime.Immutable
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.compose.folder.component.MyTuripModel
import com.on.turip.ui.compose.trip.turipselection.model.TuripPlaceModel
import com.on.turip.ui.folder.model.TuripEditModel
import com.on.turip.ui.folder.model.TuripNameStatusModel
import com.on.turip.ui.main.favorite.model.PlaceLatLngUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class TuripPlaceUiState(
    val isLoading: Boolean,
    val inputTuripName: String,
    val errorUiState: ErrorUiState,
    val showBottomSheet: Boolean,
    val turipNameStatus: TuripNameStatusModel,
    val screenMode: TuripPlaceScreenMode,
    val editModels: ImmutableList<TuripEditModel>,
    val selectedTurip: MyTuripModel,
    val places: ImmutableList<TuripPlaceModel>,
    val placesLatLng: ImmutableList<PlaceLatLngUiModel>,
) {
    val isEmpty: Boolean
        get() = places.isEmpty() && this != Idle

    companion object {
        val Idle: TuripPlaceUiState =
            TuripPlaceUiState(
                isLoading = true,
                inputTuripName = "",
                errorUiState = ErrorUiState.None,
                showBottomSheet = false,
                turipNameStatus = TuripNameStatusModel.EMPTY,
                selectedTurip = MyTuripModel.Idle,
                places = persistentListOf(),
                placesLatLng = persistentListOf(),
                editModels = persistentListOf(),
                screenMode = TuripPlaceScreenMode.MoreOption,
            )
    }
}

sealed interface TuripPlaceScreenMode {
    data object MoreOption : TuripPlaceScreenMode

    data object Edit : TuripPlaceScreenMode
}
