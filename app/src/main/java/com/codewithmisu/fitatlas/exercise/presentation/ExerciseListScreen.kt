package com.codewithmisu.fitatlas.exercise.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.codewithmisu.fitatlas.components.AppBar
import com.codewithmisu.fitatlas.components.ErrorView
import com.codewithmisu.fitatlas.components.LoadingView
import com.codewithmisu.fitatlas.core.NavigationIntent
import com.codewithmisu.fitatlas.core.UiState
import com.codewithmisu.fitatlas.core.ViewAction
import com.codewithmisu.fitatlas.core.ViewEffect
import com.codewithmisu.fitatlas.core.ViewState
import com.codewithmisu.fitatlas.utils.capitalizeFirst
import com.codewithmisu.fitatlas.exercise.domain.Exercise

@Composable
fun ExerciseListScreen(
    bodyParts: String,
    viewModel: ExerciseListViewModel,
    onItemClick: (exerciseId: String) -> Unit,
    onBackPressed: () -> Unit
) {
    // Collect the UI state from ViewModel (unidirectional data flow)
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

    // Snackbar host for showing transient messages
    val snackbarHostState = remember { SnackbarHostState() }

    // --- Trigger data load when bodyParts changes ---
    LaunchedEffect(bodyParts) {
        viewModel.onAction(ViewAction.Load(params = bodyParts))
    }

    // --- Collect one-time effects (like snackbar messages) ---
    LaunchedEffect(Unit) {
        viewModel.viewEffect.collect { effect ->
            when (effect) {
                is ViewEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                is ViewEffect.NavigateTo -> {
                    onItemClick(effect.params.toString())
                }

                is ViewEffect.NavigateBack -> {
                    onBackPressed()
                }

                else -> Unit
            }
        }
    }

    ContentView(
        bodyParts = bodyParts,
        viewState = viewState,
        onRefresh = {
            viewModel.onAction(ViewAction.ForceRefresh(params = bodyParts))
        },
        onItemClick = {
            viewModel.onAction(
                ViewAction.Navigate(
                    NavigationIntent.Forward(
                        "ExerciseDetailRoute",
                        it
                    )
                )
            )
        },
        onBackPressed = {
            viewModel.onAction(
                ViewAction.Navigate(NavigationIntent.Backward())
            )
        },
        snackbarHostState = snackbarHostState
    )
}

@Composable
private fun ContentView(
    bodyParts: String,
    viewState: ViewState<List<Exercise>>,
    onRefresh: () -> Unit,
    onItemClick: (exerciseId: String) -> Unit,
    onBackPressed: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            AppBar(
                title = bodyParts.capitalizeFirst(),
                onBackPressed = onBackPressed
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            when (val uiState = viewState.uiState) {

                // Screen not loaded yet
                is UiState.Idle -> Unit

                // Loading indicator
                is UiState.Loading -> LoadingView()

                // Error screen (full-screen)
                is UiState.Failure -> ErrorView(uiState.message)

                // Success state
                is UiState.Success<List<Exercise>> -> {
                    // Show last known exercises even if refreshing
                    ExerciseListView(
                        exercises = uiState.content,
                        onItemClick = onItemClick,
                        onRefresh = onRefresh,
                        isRefreshing = viewState.isRefreshing
                    )
                }
            }
        }
    }
}

/**
 * Composable rendering the list of exercises with pull-to-refresh.
 *
 * Uses LazyColumn for efficient scrolling.
 * Divider between items for visual separation.
 */
@Composable
private fun ExerciseListView(
    exercises: List<Exercise>,
    onItemClick: (exerciseId: String) -> Unit,
    onRefresh: () -> Unit,
    isRefreshing: Boolean
) {
    PullToRefreshBox(
        onRefresh = onRefresh,
        isRefreshing = isRefreshing
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(
                items = exercises,
                key = { _, item -> item.exerciseId } // Stable key for recomposition
            ) { index, item ->
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onItemClick(item.exerciseId)
                        }
                        .padding(vertical = 4.dp),
                    leadingContent = {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = "Exercise image: ${item.name}", // Accessibility
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .size(100.dp)
                        )
                    },
                    headlineContent = {
                        Text(
                            item.name,
                            fontWeight = FontWeight.W600
                        )
                    },
                    trailingContent = {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = "Go to ${item.name} details"
                        )
                    }
                )

                // Divider between items
                if (index != exercises.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}
