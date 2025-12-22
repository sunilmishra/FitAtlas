package com.codewithmisu.fitatlas.exercise

import com.codewithmisu.fitatlas.exercise.data.ExerciseDTO
import com.codewithmisu.fitatlas.exercise.data.ExerciseDetailDTO
import com.codewithmisu.fitatlas.exercise.data.ExerciseDetailEntity
import com.codewithmisu.fitatlas.exercise.data.ExerciseDetailResponseDTO
import com.codewithmisu.fitatlas.exercise.data.ExerciseEntity
import com.codewithmisu.fitatlas.exercise.data.ExerciseLocalSource
import com.codewithmisu.fitatlas.exercise.data.ExerciseRemoteSource
import com.codewithmisu.fitatlas.exercise.data.ExerciseRepositoryImpl
import com.codewithmisu.fitatlas.exercise.data.ExerciseResponseDTO
import com.codewithmisu.fitatlas.exercise.data.toDomain
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExerciseRepositoryTest {

    private val localSource: ExerciseLocalSource = mockk()
    private val remoteSource: ExerciseRemoteSource = mockk()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val exerciseResponseDTO = ExerciseResponseDTO(
        success = true,
        data = listOf(
            ExerciseDTO(
                exerciseId = System.currentTimeMillis().toString(),
                name = "Push-ups",
                imageUrl = "https://example.com/push-ups.jpg",
            ),
            ExerciseDTO(
                exerciseId = System.currentTimeMillis().toString(),
                name = "Squats",
                imageUrl = "https://example.com/squats.jpg"
            ),
        )
    )

    private val exerciseEntities = listOf(
        ExerciseEntity(
            exerciseId = System.currentTimeMillis().toString(),
            name = "Push-ups",
            imageUrl = "https://example.com/push-ups.jpg",
            bodyPart = "Chest"
        ),
        ExerciseEntity(
            exerciseId = System.currentTimeMillis().toString(),
            name = "Squats",
            imageUrl = "https://example.com/squats.jpg",
            bodyPart = "Chest"
        )
    )

    @Test
    fun `getExercises fetches data from remote source and caches it`() {
        testScope.runTest {
            // Given
            val fakeDtoData = exerciseResponseDTO
            coEvery { remoteSource.fetchExercises("Chest") } returns fakeDtoData

            val fakeEntities = exerciseEntities
            coEvery { localSource.getExercises("Chest") } returns exerciseEntities

            val repository = ExerciseRepositoryImpl(
                localSource = localSource,
                remoteSource = remoteSource
            )

            // When
            val result = repository.getExercises("Chest")

            // Then
            assert(result == fakeEntities.map {
                it.toDomain()
            })
        }
    }

    @Test
    fun `watchExercises emits data from local source`() {
        testScope.runTest {
            // Given
            val fakeEntities = exerciseEntities
            // Mocking watchAll() to return a Flow containing the list of entities
            every { localSource.watchExercises("Chest") } returns flowOf(fakeEntities)

            val repository = ExerciseRepositoryImpl(
                localSource = localSource,
                remoteSource = remoteSource
            )

            // When
            val resultFlow = repository.watchExercises("Chest")
            val result = resultFlow.first()

            // Then
            assert(result == fakeEntities.map { it.toDomain() })
        }
    }

    private val exerciseDetailResponseDTO = ExerciseDetailResponseDTO(
        success = true,
        data = ExerciseDetailDTO(
            exerciseId = System.currentTimeMillis().toString(),
            name = "Push-ups",
            imageUrl = "https://example.com/push-ups.jpg",
            equipments = listOf("Equipment 1", "Equipment 2"),
            bodyParts = listOf("Body Part 1", "Body Part 2"),
            exerciseType = "Exercise Type",
            targetMuscles = listOf("Target Muscle 1", "Target Muscle 2"),
            secondaryMuscles = listOf("Secondary Muscle 1", "Secondary Muscle 2"),
            videoUrl = "https://example.com/push-ups-video.mp4",
            keywords = listOf("Keyword 1", "Keyword 2"),
            overview = "Overview text",
            instructions = listOf("Instruction 1", "Instruction 2"),
            exerciseTips = listOf("Exercise Tip 1", "Exercise Tip 2"),
            variations = listOf("Variation 1", "Variation 2"),
            relatedExerciseIds = listOf("Related Exercise 1", "Related Exercise 2")
        )
    )

    private val exerciseDetailEntities = ExerciseDetailEntity(
            exerciseId = System.currentTimeMillis().toString(),
            name = "Push-ups",
            imageUrl = "https://example.com/push-ups.jpg",
            equipments = listOf("Equipment 1", "Equipment 2"),
            bodyParts = listOf("Body Part 1", "Body Part 2"),
            exerciseType = "Exercise Type",
            targetMuscles = listOf("Target Muscle 1", "Target Muscle 2"),
            secondaryMuscles = listOf("Secondary Muscle 1", "Secondary Muscle 2"),
            videoUrl = "https://example.com/push-ups-video.mp4",
            keywords = listOf("Keyword 1", "Keyword 2"),
            overview = "Overview text",
            instructions = listOf("Instruction 1", "Instruction 2"),
            exerciseTips = listOf("Exercise Tip 1", "Exercise Tip 2"),
            variations = listOf("Variation 1", "Variation 2"),
            relatedExerciseIds = listOf("Related Exercise 1", "Related Exercise 2")
    )

    @Test
    fun `getExerciseDetail fetches data from remote source and caches it`(){
        testScope.runTest {
            coEvery { remoteSource.fetchExerciseDetail("ExerciseId") } returns exerciseDetailResponseDTO
            val repository = ExerciseRepositoryImpl(
                localSource = localSource,
                remoteSource = remoteSource
            )
            coEvery { localSource.getExerciseDetail("ExerciseId") } returns exerciseDetailEntities
            val  result = repository.getExerciseDetail("ExerciseId")
            assert(result == exerciseDetailEntities.toDomain())
        }
    }
}