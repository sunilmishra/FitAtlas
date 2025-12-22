package com.codewithmisu.fitatlas.exercise.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.codewithmisu.fitatlas.components.AppBar
import com.codewithmisu.fitatlas.components.ErrorView
import com.codewithmisu.fitatlas.components.LoadingView
import com.codewithmisu.fitatlas.components.SectionDivider
import com.codewithmisu.fitatlas.components.VideoPlayer
import com.codewithmisu.fitatlas.core.UiState
import com.codewithmisu.fitatlas.exercise.domain.ExerciseDetail

@Composable
fun ExerciseDetailScreen(
    exerciseId: String,
    viewModel: ExerciseDetailViewModel,
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(exerciseId) {
        viewModel.loadExerciseDetail(exerciseId)
    }

    Scaffold(
        topBar = {
            AppBar(
                title = "Details",
                onBackPressed = onBackPressed,
                actions = {
                    IconButton(onClick = {
                        viewModel.loadExerciseDetail(exerciseId, forceRefresh = true)
                    }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                        )
                    }
                })
        }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            when (uiState) {
                is UiState.Idle -> Unit
                is UiState.Loading -> {
                    LoadingView()
                }

                is UiState.Failure -> {
                    ErrorView((uiState as UiState.Failure).message)
                }

                is UiState.Success -> {
                    val exerciseDetail = (uiState as UiState.Success).content
                    ExerciseDetailView(exerciseDetail)
                }
            }
        }
    }
}

@Composable
private fun ExerciseDetailView(detail: ExerciseDetail) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        TitleView(detail)

        VideoPlayer(
            videoUrl = detail.videoUrl, modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        )

        SectionDivider("Instructions", Modifier.padding(top = 24.dp))
        detail.instructions.mapIndexed { index, item ->
            ListItem(
                modifier = Modifier.padding(0.dp),
                leadingContent = {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Text(
                            (index + 1).toString(),
                            fontWeight = FontWeight.W600,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                },
                headlineContent = {
                    Text(item)
                }
            )
        }

        SectionDivider("Body Parts", Modifier.padding(top = 24.dp))
        ChipItem(detail.bodyParts)

        SectionDivider("Equipments", Modifier.padding(top = 24.dp))
        ChipItem(detail.equipments)

        SectionDivider("Target Muscles", Modifier.padding(top = 24.dp))
        ChipItem(detail.targetMuscles)

        SectionDivider("Tips", Modifier.padding(top = 24.dp))
        detail.exerciseTips.map {
            ListItem(headlineContent = {
                Text(it)
            })
        }

        SectionDivider("Variations", Modifier.padding(top = 24.dp))
        detail.variations.map {
            ListItem(headlineContent = {
                Text(it)
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TitleView(detail: ExerciseDetail) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    SectionDivider(
        detail.name,
        infoAction = {
            showBottomSheet = true
        }
    )

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false }
        ) {
            Text(
                detail.overview,
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            )
        }
    }
}

@Composable
private fun ChipItem(items: List<String>) {
    Row {
        items.map {
            SuggestionChip(
                modifier = Modifier.padding(horizontal = 8.dp),
                enabled = false,
                onClick = {},
                label = {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
        }
    }
}
