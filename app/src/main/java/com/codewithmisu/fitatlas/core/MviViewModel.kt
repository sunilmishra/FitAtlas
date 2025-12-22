package com.codewithmisu.fitatlas.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MviViewModel for generic MVI architecture.
 *
 * T = Type of data/content handled by this ViewModel
 *
 * This ViewModel handles:
 * 1. State management via [ViewState]
 * 2. One-time effects via [ViewEffect]
 * 3. Generic user actions via [ViewAction]
 */
abstract class MviViewModel<T> : ViewModel() {

    // ----------------------------
    // StateFlow for current screen state
    // ----------------------------
    private val _viewState = MutableStateFlow(ViewState<T>())
    val viewState = _viewState.asStateFlow()

    // ----------------------------
    // SharedFlow for one-time UI effects (snackbar, navigation, dialogs)
    // ----------------------------
    private val _viewEffect = MutableSharedFlow<ViewEffect>()
    val viewEffect = _viewEffect.asSharedFlow()

    // ----------------------------
    // State update helper (Reducer)
    // ----------------------------
    /**
     * Updates the current ViewState using a reducer function.
     *
     * A reducer is a pure function that receives the current state and returns a new state.
     * This promotes **immutable, predictable state updates**.
     *
     * Example:
     * ```
     * emitState { current ->
     *     current.copy(uiState = UiState.Loading)
     * }
     * ```
     */
    protected fun emitState(reducer: (ViewState<T>) -> ViewState<T>) {
        _viewState.update { currentState -> reducer(currentState) }
    }

    // ----------------------------
    // Effect emission helper
    // ----------------------------
    /**
     * Emits a one-time UI effect such as showing an error or navigating.
     */
    protected fun emitEffect(effect: ViewEffect) {
        viewModelScope.launch {
            _viewEffect.emit(effect)
        }
    }

    // ----------------------------
    // Abstract function for handling screen-specific actions
    // ----------------------------
    /**
     * Handle user actions (intents) from the UI.
     *
     * @param action The user action or intent.
     */
    abstract fun onAction(action: ViewAction)


    // ==========================================================================================//
    // ==================== Convenience helpers for common actions ==============================//
    // ==========================================================================================//
    /**
     * Set loading state.
     */
    protected fun emitLoadingState() {
        emitState { it.copy(uiState = UiState.Loading) }
    }

    /**
     * Set success state with loaded data.
     *
     * @param data The loaded data.
     */
    protected fun emitSuccessState(data: T) {
        emitState {
            it.copy(
                uiState = UiState.Success(content = data),
                isRefreshing = false,
            )
        }
    }

    /**
     * Set failure state with an optional error message.
     */
    protected fun emitFailureState(message: String, throwable: Throwable?) {
        emitState {
            it.copy(
                uiState = UiState.Failure(
                    message = message,
                    throwable = throwable
                ),
                isRefreshing = false
            )
        }
    }

    /**
     * Set Refresh state (Pull Down to Refresh)
     */
    protected fun emitRefreshState(isRefreshing: Boolean = false) {
        emitState { it.copy(isRefreshing = isRefreshing) }
    }

    /**
     *  Show an Success message effect.
     */
    protected fun emitSuccessEffect(message: String) {
        emitEffect(ViewEffect.ShowMessage(message))
    }

    /**
     *  Show an failure message effect.
     */
    protected fun emitFailureEffect(message: String) {
        emitEffect(
            ViewEffect.ShowMessage(
                message = message,
                type = MessageType.Failure
            )
        )
    }
}
