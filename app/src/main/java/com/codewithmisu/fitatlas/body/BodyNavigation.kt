package com.codewithmisu.fitatlas.body

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.codewithmisu.fitatlas.body.presentation.BodyScreen
import com.codewithmisu.fitatlas.body.presentation.BodyViewModel
import kotlinx.serialization.Serializable

@Serializable
data object BodyPartListRoute : NavKey

fun EntryProviderScope<NavKey>.bodyPartsNavigator(
    onNavigateToExercise: (name: String) -> Unit
) {
    entry<BodyPartListRoute> {
        val bodyViewModel: BodyViewModel = hiltViewModel()
        BodyScreen(
            viewModel = bodyViewModel,
            onItemClicked = {
                onNavigateToExercise(it)
            }
        )
    }
}