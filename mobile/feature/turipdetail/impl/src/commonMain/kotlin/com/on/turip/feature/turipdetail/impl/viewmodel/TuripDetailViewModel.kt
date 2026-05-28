package com.on.turip.feature.turipdetail.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.on.turip.core.domain.repository.TuripRepository
import com.on.turip.core.ui.BaseViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TuripDetailViewModel(
    private val turipId: Long,
    private val turipRepository: TuripRepository,
) : BaseViewModel<TuripDetailIntent, TuripDetailState, TuripDetailEffect>(TuripDetailState()) {

    init {
        loadTuripDetail()
    }

    override fun onIntent(intent: TuripDetailIntent) {
        when (intent) {
            TuripDetailIntent.Retry -> loadTuripDetail()
            TuripDetailIntent.ShowMemberSheet -> updateState { copy(showMemberSheet = true) }
            TuripDetailIntent.DismissMemberSheet -> updateState { copy(showMemberSheet = false) }
            TuripDetailIntent.ShowMoreOptions -> updateState { copy(showMoreOptionsSheet = true) }
            TuripDetailIntent.DismissMoreOptions -> updateState { copy(showMoreOptionsSheet = false) }
            TuripDetailIntent.ShowDeleteDialog -> updateState {
                copy(showMoreOptionsSheet = false, showDeleteDialog = true)
            }
            TuripDetailIntent.DismissDeleteDialog -> updateState { copy(showDeleteDialog = false) }
            TuripDetailIntent.ConfirmDelete -> confirmDelete()
            TuripDetailIntent.ShowRenameSheet -> updateState {
                copy(
                    showMoreOptionsSheet = false,
                    showRenameSheet = true,
                    inputName = turip?.name.orEmpty(),
                )
            }
            TuripDetailIntent.DismissRenameSheet -> updateState {
                copy(showRenameSheet = false, inputName = "")
            }
            is TuripDetailIntent.UpdateName -> updateState { copy(inputName = intent.name) }
            TuripDetailIntent.ConfirmRename -> confirmRename()
            is TuripDetailIntent.RemovePlace -> removePlace(intent.turipPlaceId)
        }
    }

    private fun loadTuripDetail() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, isError = false) }

            val turipDeferred = async { turipRepository.getTurip(turipId) }
            val placesDeferred = async { turipRepository.getTuripPlaces(turipId) }

            val turipResult = turipDeferred.await()
            val placesResult = placesDeferred.await()

            if (turipResult.isFailure) {
                updateState { copy(isLoading = false, isError = true) }
                return@launch
            }

            val turip = turipResult.getOrThrow()
            val places = placesResult.getOrElse { emptyList() }

            val members = if (turip.isShared) {
                turipRepository.getTuripMembers(turipId).getOrElse { emptyList() }
            } else {
                emptyList()
            }

            updateState {
                copy(
                    turip = turip,
                    places = places.toImmutableList(),
                    members = members.toImmutableList(),
                    isLoading = false,
                )
            }
        }
    }

    private fun confirmDelete() {
        val turip = currentState.turip ?: return
        updateState { copy(showDeleteDialog = false) }
        viewModelScope.launch {
            val result = if (turip.isShared) {
                turipRepository.exitTurip(turipId)
            } else {
                turipRepository.deleteTurip(turipId)
            }
            result
                .onSuccess { emitEffect(TuripDetailEffect.NavigateBack) }
                .onFailure { emitEffect(TuripDetailEffect.ShowError) }
        }
    }

    private fun confirmRename() {
        val name = currentState.inputName.trim()
        if (name.isBlank() || currentState.isRenaming) return
        viewModelScope.launch {
            updateState { copy(isRenaming = true) }
            turipRepository.renameTurip(turipId, name)
                .onSuccess {
                    updateState {
                        copy(
                            turip = turip?.copy(name = name),
                            showRenameSheet = false,
                            inputName = "",
                            isRenaming = false,
                        )
                    }
                }
                .onFailure {
                    updateState { copy(isRenaming = false) }
                    emitEffect(TuripDetailEffect.ShowError)
                }
        }
    }

    private fun removePlace(turipPlaceId: Long) {
        val place = currentState.places.find { it.turipId == turipId && it.place.id == turipPlaceId }
        viewModelScope.launch {
            turipRepository.removeTuripPlace(turipId, turipPlaceId)
                .onSuccess {
                    updateState {
                        copy(places = places.filter { it.place.id != turipPlaceId }.toImmutableList())
                    }
                    emitEffect(TuripDetailEffect.ShowPlaceRemoved(place?.place?.name.orEmpty()))
                }
                .onFailure { emitEffect(TuripDetailEffect.ShowError) }
        }
    }
}
