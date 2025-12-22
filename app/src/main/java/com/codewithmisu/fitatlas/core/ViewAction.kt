package com.codewithmisu.fitatlas.core

/**
 * Represents user-driven actions (Intents) in MVI.
 *
 * ViewAction is the single entry point for all UI interactions.
 * The View sends actions to the ViewModel, which then produces
 * State updates and/or one-time ViewEffects.
 *
 * Not every screen needs to handle every action.
 */
sealed interface ViewAction {

    /**
     * Initial or parameterized data load.
     *
     * Used when a screen is first displayed or needs to load data
     * based on input parameters.
     */
    data class Load(val params: Any? = null) : ViewAction

    /**
     * Explicit user-initiated refresh.
     *
     * Typically triggered by pull-to-refresh gestures.
     * Should not be confused with automatic reloads.
     */
    data class ForceRefresh(val params: Any? = null) : ViewAction

    /**
     * Navigation intent triggered by the user.
     *
     * Navigation is modeled as an Action (Intent) in MVI to keep
     * unidirectional data flow:
     *
     * User → Action → ViewModel → ViewEffect → UI
     *
     * The ViewModel does NOT perform navigation directly.
     * Instead, it emits a ViewEffect that the host handles.
     */
    data class Navigate(val intent: NavigationIntent) : ViewAction
}

/**
 * Describes *how* navigation should occur.
 *
 * This abstraction keeps feature modules decoupled from
 * navigation implementation details (Navigation 3 back stack).
 *
 * The host interprets these intents and mutates the back stack.
 */
sealed interface NavigationIntent {

    /**
     * Navigate forward by pushing a new destination onto the back stack.
     *
     * @param route Identifier for the destination screen.
     * @param params Optional data to pass to the destination.
     */
    data class Forward(
        val route: String,
        val params: Any? = null
    ) : NavigationIntent

    /**
     * Navigate backward by popping destinations from the back stack.
     *
     * @param toRoute Optional route to pop back to.
     */
    data class Backward(
        val toRoute: String? = null,
    ) : NavigationIntent

    /**
     * Replace the current destination.
     *
     * @param route Destination to navigate to.
     * @param params Optional data to pass to the destination.
     * @param clearStack If true, clears the entire back stack before navigating.
     *                   Useful for flows like logout or onboarding completion.
     */
    data class Replace(
        val route: String,
        val params: Any? = null,
        val clearStack: Boolean = false
    ) : NavigationIntent
}
