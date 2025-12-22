package com.codewithmisu.fitatlas

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.codewithmisu.fitatlas.body.BodyPartListRoute
import com.codewithmisu.fitatlas.body.bodyPartsNavigator
import com.codewithmisu.fitatlas.exercise.ExerciseListRoute
import com.codewithmisu.fitatlas.exercise.exerciseNavigator

@Composable
fun AppNavigation() {
    val backStack = rememberNavBackStack(BodyPartListRoute)

    NavDisplay(
        backStack = backStack,
        onBack = {
            backStack.removeLastOrNull()
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            bodyPartsNavigator(
                onNavigateToExercise = {
                    backStack.add(ExerciseListRoute(bodyPart = it))
                }
            )
            exerciseNavigator(backStack = backStack)
        },
    )
}