package com.on.turip.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Compose 및 Native 화면에서 공통으로 사용할 ViewModel 기본 뼈대를 제공합니다.
 *
 * 각 화면에서 `UiIntent`, `UiState`, `UiEffect` 를 구현하여 입력-상태-이벤트 사이의 흐름을 명확히 분리할 수 있습니다.
 *
 * ### Example
 * ```kotlin
 * data class LoginState(val isLoading: Boolean = false) : UiState
 *
 * sealed interface LoginIntent : UiIntent {
 *     data object SubmitClicked : LoginIntent
 * }
 *
 * sealed interface LoginEffect : UiEffect {
 *     data object NavigateHome : LoginEffect
 * }
 *
 * class LoginViewModel : BaseViewModel<LoginIntent, LoginState, LoginEffect>(LoginState()) {
 *     override fun onIntent(intent: LoginIntent) {
 *         when (intent) {
 *             LoginIntent.SubmitClicked -> {
 *                 updateState { copy(isLoading = true) }
 *                 emitEffect(LoginEffect.NavigateHome)
 *             }
 *         }
 *     }
 * }
 * ```
 */
abstract class BaseViewModel<INTENT : UiIntent, STATE : UiState, EFFECT : UiEffect>(
    initialState: STATE,
) : ViewModel() {
    private val _uiState = MutableStateFlow(initialState)
    val uiState get() = _uiState.asStateFlow()

    private val _effect: Channel<EFFECT> = Channel(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    protected val currentState: STATE
        get() = _uiState.value

    abstract fun onIntent(intent: INTENT)

    protected fun updateState(reduce: STATE.() -> STATE) {
        _uiState.update { currentState.reduce() }
    }

    protected fun emitEffect(effect: EFFECT) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
