package com.codewithmisu.fitatlas.exercise.presentation

import androidx.lifecycle.viewModelScope
import com.codewithmisu.fitatlas.core.MviViewModel
import com.codewithmisu.fitatlas.core.NavigationIntent
import com.codewithmisu.fitatlas.core.UiState
import com.codewithmisu.fitatlas.core.ViewAction
import com.codewithmisu.fitatlas.core.ViewEffect
import com.codewithmisu.fitatlas.exercise.domain.Exercise
import com.codewithmisu.fitatlas.exercise.domain.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseListViewModel @Inject constructor(
    private val repository: ExerciseRepository
) : MviViewModel<List<Exercise>>() {

    override fun onAction(action: ViewAction) {
        when (action) {
            is ViewAction.Load -> loadExercise(params = action.params.toString())
            is ViewAction.ForceRefresh -> forceRefresh(params = action.params.toString())
            is ViewAction.Navigate -> navigate(action.intent)
        }
    }

    /**
     * Load exercises data and emit UI state accordingly.
     * Uses a Flow to watch cached body parts and fetch fresh data.
     */
    private fun loadExercise(params: String) {
        // Check if the same data is already being loaded
        if (viewState.value.uiState is UiState.Success) {
            return
        }

        viewModelScope.launch {
            repository.watchExercises(bodyPart = params)
                .onStart {
                    emitLoadingState()
                    repository.getExercises(bodyPart = params)
                }
                .catch { e ->
                    emitFailureState(
                        message = e.message ?: "Failed to load exercises...",
                        throwable = e,
                    )
                }
                .collect {
                    emitSuccessState(data = it)
                }
        }
    }

    /**
     * Force refresh exercises
     * Emits refreshing state during the operation.
     */
    private fun forceRefresh(params: String) {
        viewModelScope.launch {
            try {
                emitRefreshState(isRefreshing = true)
                repository.getExercises(bodyPart = params, forceRefresh = true)
            } catch (e: Exception) {
                emitRefreshState(isRefreshing = false)
                emitFailureEffect(e.message ?: "Failed to refresh exercises...")
            }
        }
    }

    /**
     * Handle navigation intents from the UI.
     * - Forward: navigate to a new screen
     * - Backward: navigate back
     */
    private fun navigate(intent: NavigationIntent) {
        if (intent is NavigationIntent.Forward) {
            emitEffect(
                ViewEffect.NavigateTo(
                    route = intent.route,
                    params = intent.params
                )
            )
        }

        if (intent is NavigationIntent.Backward) {
            emitEffect(
                ViewEffect.NavigateBack()
            )
        }
    }
}