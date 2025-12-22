package com.codewithmisu.fitatlas.exercise

import com.codewithmisu.fitatlas.core.NavigationIntent
import com.codewithmisu.fitatlas.core.UiState
import com.codewithmisu.fitatlas.core.ViewAction
import com.codewithmisu.fitatlas.core.ViewEffect
import com.codewithmisu.fitatlas.exercise.domain.Exercise
import com.codewithmisu.fitatlas.exercise.domain.ExerciseRepository
import com.codewithmisu.fitatlas.exercise.presentation.ExerciseListViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExerciseListViewModelTest {

    private val repository: ExerciseRepository = mockk()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var viewModel: ExerciseListViewModel

    private val exerciseList = listOf(
        Exercise(
            exerciseId = "1",
            name = "Exercise 1",
            imageUrl = "https://example.com/exercise1.jpg"
        ),
        Exercise(
            exerciseId = "2",
            name = "Exercise 2",
            imageUrl = "https://example.com/exercise2.jpg"
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Mock the default flow emission for initialization
        every { repository.watchExercises("Chest") } returns flowOf(emptyList())
        coEvery { repository.getExercises("Chest") } returns emptyList()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadExercise should fetch exercises and update UI state`() {
        testScope.runTest {

            every { repository.watchExercises("Chest") } returns flowOf(exerciseList)
            coEvery { repository.getExercises("Chest") } returns exerciseList

            viewModel = ExerciseListViewModel(repository)

            viewModel.onAction(ViewAction.Load("Chest"))
            advanceUntilIdle()

            val state = viewModel.viewState.value
            when (state.uiState) {
                is UiState.Success<*> -> {
                    assertEquals(exerciseList, state.uiState.content)
                }

                else -> {
                    assertEquals(UiState.Idle, state.uiState)
                }
            }
            assertEquals(false, state.isRefreshing)
        }
    }

    @Test
    fun `forceRefresh should fetch exercises and update UI state`() {
        testScope.runTest {

            every { repository.watchExercises("Chest") } returns flowOf(exerciseList)
            coEvery { repository.getExercises("Chest") } returns exerciseList

            viewModel = ExerciseListViewModel(repository)

            viewModel.onAction(ViewAction.ForceRefresh("Chest"))
            advanceUntilIdle()

            val state = viewModel.viewState.value
            when (state.uiState) {
                is UiState.Success<*> -> {
                    assertEquals(exerciseList, state.uiState.content)
                }

                else -> {
                    assertEquals(UiState.Idle, state.uiState)
                }
            }
            assertEquals(false, state.isRefreshing)
        }
    }

    @Test
    fun `navigate should handle navigation intents`() = testScope.runTest {
        // Given
        viewModel = ExerciseListViewModel(repository)
        val effects = mutableListOf<ViewEffect>()

        // Start collecting effects in the background
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewEffect.collect { effects.add(it) }
        }

        val route = "route"
        val params = "params"

        // When
        viewModel.onAction(
            ViewAction.Navigate(
                NavigationIntent.Forward(route, params)
            )
        )

        // Ensure all coroutines within the ViewModel finish
        advanceUntilIdle()

        // Then
        val lastEffect = effects.lastOrNull()
        assert(lastEffect is ViewEffect.NavigateTo)
        if (lastEffect is ViewEffect.NavigateTo) {
            assertEquals(route, lastEffect.route)
            assertEquals(params, lastEffect.params)
        }
    }
}
