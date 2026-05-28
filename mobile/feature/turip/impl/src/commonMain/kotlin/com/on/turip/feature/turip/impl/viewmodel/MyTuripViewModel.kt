package com.on.turip.feature.turip.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.on.turip.core.domain.repository.TuripRepository
import com.on.turip.core.model.Turip
import com.on.turip.core.ui.BaseViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

private const val MAX_NAME_LENGTH = 20

class MyTuripViewModel(
    private val turipRepository: TuripRepository,
) : BaseViewModel<MyTuripIntent, MyTuripState, MyTuripEffect>(MyTuripState()) {

    init {
        loadTurips()
    }

    override fun onIntent(intent: MyTuripIntent) {
        when (intent) {
            MyTuripIntent.LoadTurips -> loadTurips()
            is MyTuripIntent.SelectTab -> updateState { copy(selectedTab = intent.tab) }
            MyTuripIntent.ShowAddBottomSheet -> updateState { copy(showAddBottomSheet = true) }
            MyTuripIntent.DismissAddBottomSheet -> updateState {
                copy(showAddBottomSheet = false, inputTuripName = "", isCreatingTurip = false)
            }
            is MyTuripIntent.UpdateTuripName -> {
                if (intent.name.length <= MAX_NAME_LENGTH) {
                    updateState { copy(inputTuripName = intent.name) }
                }
            }
            MyTuripIntent.ConfirmAddTurip -> addTurip()
            is MyTuripIntent.ShowDeleteDialog -> updateState {
                copy(dialogState = MyTuripDialogState.RemoveTurip(intent.turip))
            }
            MyTuripIntent.DismissDeleteDialog -> updateState { copy(dialogState = null) }
            is MyTuripIntent.ConfirmDelete -> deleteTurip(intent.turip)
        }
    }

    private fun loadTurips() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, isError = false) }
            turipRepository.getTurips()
                .onSuccess { turips ->
                    updateState { copy(turips = turips.toImmutableList(), isLoading = false) }
                }
                .onFailure {
                    updateState { copy(isLoading = false, isError = true) }
                    emitEffect(MyTuripEffect.ShowLoadError)
                }
        }
    }

    private fun addTurip() {
        val name = currentState.inputTuripName.trim()
        if (name.isBlank() || currentState.isCreatingTurip) return
        viewModelScope.launch {
            updateState { copy(isCreatingTurip = true) }
            turipRepository.createTurip(name)
                .onSuccess { newTurip ->
                    updateState {
                        copy(
                            turips = (turips + newTurip).toImmutableList(),
                            showAddBottomSheet = false,
                            inputTuripName = "",
                            isCreatingTurip = false,
                        )
                    }
                    emitEffect(MyTuripEffect.ShowTuripAdded(name))
                }
                .onFailure {
                    updateState { copy(isCreatingTurip = false) }
                    emitEffect(MyTuripEffect.ShowActionError)
                }
        }
    }

    private fun deleteTurip(turip: Turip) {
        updateState { copy(dialogState = null) }
        viewModelScope.launch {
            val result = if (turip.isShared) {
                turipRepository.exitTurip(turip.id)
            } else {
                turipRepository.deleteTurip(turip.id)
            }
            result
                .onSuccess {
                    updateState {
                        copy(turips = turips.filter { it.id != turip.id }.toImmutableList())
                    }
                    emitEffect(MyTuripEffect.ShowTuripDeleted(turip.name))
                }
                .onFailure { emitEffect(MyTuripEffect.ShowActionError) }
        }
    }
}
