package com.on.turip.feature.turipdetail.impl

import androidx.compose.runtime.Immutable
import com.on.turip.core.ui.error.ErrorUiState
import com.on.turip.core.ui.model.namestatus.TuripNameStatusModel
import com.on.turip.core.ui.model.turip.TuripEditModel
import com.on.turip.feature.turipdetail.impl.model.MyTuripModel
import com.on.turip.feature.turipdetail.impl.model.TuripPlaceModel
import com.on.turip.feature.turipdetail.impl.model.turip.PlaceLatLngUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class TuripDetailUiState(
    val isLoading: Boolean,
    val inputTuripName: String,
    val errorUiState: ErrorUiState,
    val showMoreOptionBottomSheet: Boolean,
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
                showMoreOptionBottomSheet = false,
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
