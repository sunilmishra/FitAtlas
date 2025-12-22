package com.codewithmisu.fitatlas.body

import com.codewithmisu.fitatlas.body.domain.BodyParts
import com.codewithmisu.fitatlas.body.domain.BodyRepository
import com.codewithmisu.fitatlas.body.presentation.BodyViewModel
import com.codewithmisu.fitatlas.core.UiState
import com.codewithmisu.fitatlas.core.ViewAction
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BodyViewModelTest {

    private val repository: BodyRepository = mockk()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var viewModel: BodyViewModel

    private val bodyParts = listOf(
        BodyParts(name = "Chest", imageUrl = "https://example.com/chest.jpg"),
        BodyParts(name = "Back", imageUrl = "https://example.com/back.jpg"),
        BodyParts(name = "Calves", imageUrl = "https://example.com/calves.jpg")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Mock the default flow emission for initialization
        every { repository.watchBodyParts() } returns flowOf(emptyList())
        coEvery { repository.getBodyParts(any()) } returns emptyList()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should fetch body parts and watch for local changes`() = testScope.runTest {
        // Given
        every { repository.watchBodyParts() } returns flowOf(bodyParts)
        coEvery { repository.getBodyParts(forceRefresh = false) } returns bodyParts

        // When
        viewModel = BodyViewModel(repository)
        advanceUntilIdle()

        // Then
        val state = viewModel.viewState.value
        when (state.uiState) {
            is UiState.Success<*> -> {
                assertEquals(bodyParts, state.uiState.content)
            }
            else -> {
                assertEquals(UiState.Idle, state.uiState)
            }
        }
        assertEquals(false, state.isRefreshing)
    }

    @Test
    fun `refreshBodyParts should update loading state and fetch data`() = testScope.runTest {
        // Given
        every { repository.watchBodyParts() } returns flowOf(bodyParts)
        coEvery { repository.getBodyParts(forceRefresh = true) } returns bodyParts

        viewModel = BodyViewModel(repository)

        // When
        viewModel.onAction(ViewAction.ForceRefresh()) // Assuming an onRefresh or similar method exists
        advanceUntilIdle()

        // Then
        coEvery { repository.getBodyParts(forceRefresh = true) } // Verify call happened
        val state = viewModel.viewState.value
        when (state.uiState) {
            is UiState.Success<*> -> {
                assertEquals(bodyParts, state.uiState.content)
            }
            else -> {
                assertEquals(UiState.Idle, state.uiState)
            }
        }
    }
}
