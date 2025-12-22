package com.codewithmisu.fitatlas.core

/**
 * Generic one-time UI effects (side-effects).
 * These are events emitted by the ViewModel that the UI should react to once.
 * Examples: showing a Snackbar, Toast, BottomSheet, opening a Dialog, or navigating.
 */
sealed interface ViewEffect {

    /**
     * Show a message to the user (e.g., Snackbar, Toast, BottomSheet, Dialog).
     *
     * @param message The text message to display.
     * @param type The style/type of the message (success or failure). Default is [MessageType.Success].
     *
     */
    data class ShowMessage(
        val message: String,
        val type: MessageType = MessageType.Success,
    ) : ViewEffect

    /**
     * Navigate to another screen.
     *
     * @param params Optional data to pass to the destination screen.
     * @param route Optional string identifier for the navigation target.
     *              Can be used in modular navigation setups.
     */
    data class NavigateTo(
        val params: Any? = null,
        val route: String? = null
    ) : ViewEffect

    /**
     * Navigate back to a previous screen.
     *
     * @param route Optional identifier of the route to pop back to.
     *              If null, the last screen in the back stack is popped.
     */
    data class NavigateBack(val route: String? = null) : ViewEffect

    /**
     * Navigate to another screen.
     *
     * @param params Optional data to pass to the destination screen.
     * @param route Optional string identifier for the navigation target.
     *              Can be used in modular navigation setups.
     * @param clearStack If true, clears the entire back stack before navigating.
     *                   Useful for flows like logout or onboarding completion.
     */
    data class NavigateReplace(
        val params: Any? = null,
        val route: String? = null,
        val clearStack: Boolean = false
    ) : ViewEffect
}

/**
 * Represents the style/type of a message (success or failure).
 * Can be extended with Warning, Info, etc., in the future.
 */
sealed interface MessageType {
    object Success : MessageType
    object Failure : MessageType
}

