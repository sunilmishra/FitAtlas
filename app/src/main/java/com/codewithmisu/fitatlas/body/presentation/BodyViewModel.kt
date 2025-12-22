package com.codewithmisu.fitatlas.body.presentation

import androidx.lifecycle.viewModelScope
import com.codewithmisu.fitatlas.body.domain.BodyParts
import com.codewithmisu.fitatlas.body.domain.BodyRepository
import com.codewithmisu.fitatlas.core.MviViewModel
import com.codewithmisu.fitatlas.core.NavigationIntent
import com.codewithmisu.fitatlas.core.ViewAction
import com.codewithmisu.fitatlas.core.ViewEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Body screen following the MVI pattern.
 *
 * Responsibilities:
 * 1. Load body parts data from the repository.
 * 2. Handle pull-to-refresh.
 * 3. Emit one-time UI effects (snackbar, navigation).
 */
@HiltViewModel
class BodyViewModel @Inject constructor(
    private val repository: BodyRepository
) : MviViewModel<List<BodyParts>>() {

    // Trigger initial data load when ViewModel is created
    init {
        onAction(ViewAction.Load())
    }

    /**
     * Handles all ViewActions (intents) from the UI.
     * - Load: fetches body parts
     * - ForceRefresh: refreshes data
     * - Navigate: triggers navigation effect
     */
    override fun onAction(action: ViewAction) {
        when (action) {
            is ViewAction.Load -> loadBodyParts()
            is ViewAction.ForceRefresh -> forceRefresh()
            is ViewAction.Navigate -> navigate(action.intent)
        }
    }

    /**
     * Load body parts data and emit UI state accordingly.
     * Uses a Flow to watch cached body parts and fetch fresh data.
     *
     */
    private fun loadBodyParts() {
        viewModelScope.launch {
            repository.watchBodyParts()
                .onStart {
                    emitLoadingState()
                    repository.getBodyParts()
                }
                .catch { e ->
                    emitFailureState(
                        message = e.message ?: "Failed to load body parts...",
                        throwable = e,
                    )
                }
                .collect { bodyParts ->
                    emitSuccessState(data = bodyParts)
                }
        }
    }

    /**
     * Force refresh body parts data.
     * Emits refreshing state during the operation.
     */
    private fun forceRefresh() {
        viewModelScope.launch {
            try {
                emitRefreshState(isRefreshing = true)
                repository.getBodyParts(forceRefresh = true)
            } catch (e: Exception) {
                emitRefreshState(isRefreshing = false)
                emitFailureEffect(e.message ?: "Failed to refresh body parts...")
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
