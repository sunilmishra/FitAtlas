package com.codewithmisu.fitatlas.body.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.codewithmisu.fitatlas.body.domain.BodyParts
import com.codewithmisu.fitatlas.components.AppBar
import com.codewithmisu.fitatlas.components.ErrorView
import com.codewithmisu.fitatlas.components.LoadingView
import com.codewithmisu.fitatlas.core.*
import com.codewithmisu.fitatlas.utils.capitalizeFirst

/**
 * Entry composable for the Body screen.
 * - Collects UI state and side-effects from the ViewModel.
 * - Displays the main BodyPartView composable.
 * - Handles one-time effects like navigation and snackbar.
 */
@Composable
fun BodyScreen(
    viewModel: BodyViewModel,
    onItemClicked: (String) -> Unit
) {
    // Collect UI state as Compose state
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

    // SnackbarHostState for transient messages
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect one-time effects (ViewEffect) from ViewModel
    LaunchedEffect(Unit) {
        viewModel.viewEffect.collect { effect ->
            when (effect) {
                is ViewEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                is ViewEffect.NavigateTo -> {
                    onItemClicked(effect.params.toString())
                }

                else -> Unit
            }
        }
    }

    // Main screen content
    BodyPartView(
        viewState = viewState,
        onBodyPartClick = { bodyPart ->
            // Trigger navigation action in ViewModel when body part clicked
            viewModel.onAction(
                ViewAction.Navigate(
                    NavigationIntent.Forward(
                        route = "ExerciseListRoute",
                        params = bodyPart
                    )
                )
            )
        },
        onRetry = { viewModel.onAction(ViewAction.Load()) },
        onRefresh = { viewModel.onAction(ViewAction.ForceRefresh()) },
        snackbarHostState = snackbarHostState
    )
}


/**
 * Main composable for Body Parts UI.
 * - Displays AppBar, loading, error, or content based on ViewState.
 * - Handles refresh and retry actions.
 */
@Composable
private fun BodyPartView(
    viewState: ViewState<List<BodyParts>>,
    onBodyPartClick: (String) -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = { AppBar(title = "FitAtlas") },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            when (val uiState = viewState.uiState) {
                is UiState.Idle -> Unit
                is UiState.Loading -> LoadingView()
                is UiState.Failure -> ErrorView(uiState.message, onRetry)
                is UiState.Success<List<BodyParts>> -> BodyPartListView(
                    bodies = uiState.content,
                    onBodyPartClick = onBodyPartClick,
                    onRefresh = onRefresh,
                    isRefreshing = viewState.isRefreshing
                )
            }
        }
    }
}


/**
 * Displays the grid of BodyParts with pull-to-refresh support.
 */
@Composable
private fun BodyPartListView(
    bodies: List<BodyParts>,
    onBodyPartClick: (String) -> Unit,
    onRefresh: () -> Unit,
    isRefreshing: Boolean
) {
    PullToRefreshBox(
        onRefresh = onRefresh,
        isRefreshing = isRefreshing
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(bodies) { body ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onBodyPartClick(body.name) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Image for body part
                        AsyncImage(
                            model = body.imageUrl,
                            contentDescription = body.name,
                            modifier = Modifier.clip(RoundedCornerShape(4.dp))
                        )
                        Spacer(Modifier.height(8.dp))
                        // Body part name
                        Text(body.name.capitalizeFirst(), fontWeight = FontWeight.W600)
                    }
                }
            }
        }
    }
}


// ===============================================================================================//
// ===================================== PREVIEW =================================================//
// ===============================================================================================//

// Sample data for previews
private val sampleBodies = listOf(
    BodyParts(name = "Chest", imageUrl = "https://cdn.exercisedb.dev/bodyparts/chest.webp"),
    BodyParts(name = "Back", imageUrl = "https://cdn.exercisedb.dev/bodyparts/back.webp"),
    BodyParts(name = "Calves", imageUrl = "https://cdn.exercisedb.dev/bodyparts/calves.webp")
)

/**
 * Preview for BodyScreen with Success state.
 */
@Preview(showBackground = true)
@Composable
fun BodyScreenPreview_Success() {
    BodyPartView(
        viewState = ViewState(
            uiState = UiState.Success(sampleBodies),
            isRefreshing = false
        ),
        onBodyPartClick = {},
        onRetry = {},
        onRefresh = {},
        snackbarHostState = remember { SnackbarHostState() }
    )
}

/**
 * Preview for BodyPartListView standalone.
 */
@Preview(showBackground = true)
@Composable
fun BodyPartListViewPreview() {
    BodyPartListView(
        bodies = sampleBodies,
        onBodyPartClick = {},
        onRefresh = {},
        isRefreshing = false
    )
}
