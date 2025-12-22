package com.codewithmisu.fitatlas.exercise

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.codewithmisu.fitatlas.exercise.presentation.ExerciseDetailScreen
import com.codewithmisu.fitatlas.exercise.presentation.ExerciseDetailViewModel
import com.codewithmisu.fitatlas.exercise.presentation.ExerciseListScreen
import com.codewithmisu.fitatlas.exercise.presentation.ExerciseListViewModel
import kotlinx.serialization.Serializable

@Serializable
data class ExerciseListRoute(val bodyPart: String) : NavKey

@Serializable
data class ExerciseDetailRoute(val exerciseId: String) : NavKey

fun EntryProviderScope<NavKey>.exerciseNavigator(
    backStack: NavBackStack<NavKey>
) {
    entry<ExerciseListRoute> {
        val exerciseListViewModel: ExerciseListViewModel = hiltViewModel()
        ExerciseListScreen(
            bodyParts = it.bodyPart,
            viewModel = exerciseListViewModel,
            onItemClick = { exerciseId ->
                backStack.add(ExerciseDetailRoute(exerciseId = exerciseId))
            },
            onBackPressed = {
                backStack.removeLastOrNull()
            }
        )
    }

    entry<ExerciseDetailRoute> {
        val exerciseDetailViewModel: ExerciseDetailViewModel = hiltViewModel()
        ExerciseDetailScreen(
            exerciseId = it.exerciseId,
            viewModel = exerciseDetailViewModel,
            onBackPressed = {
                backStack.removeLastOrNull()
            }
        )
    }
}