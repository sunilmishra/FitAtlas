package com.codewithmisu.fitatlas.body

import com.codewithmisu.fitatlas.body.data.BodyEntity
import com.codewithmisu.fitatlas.body.data.BodyLocalSource
import com.codewithmisu.fitatlas.body.data.BodyPartDTO
import com.codewithmisu.fitatlas.body.data.BodyPartResponseDTO
import com.codewithmisu.fitatlas.body.data.BodyRemoteSource
import com.codewithmisu.fitatlas.body.data.BodyRepositoryImpl
import com.codewithmisu.fitatlas.body.data.toDomain
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
class BodyRepositoryTest {

    private val localSource: BodyLocalSource = mockk()
    private val remoteSource: BodyRemoteSource = mockk()

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

    private val bodyResponseDTO = BodyPartResponseDTO(
        success = true,
        data = listOf(
            BodyPartDTO(name = "Chest", imageUrl = "https://example.com/chest.jpg"),
            BodyPartDTO(name = "Back", imageUrl = "https://example.com/back.jpg"),
            BodyPartDTO(name = "Calves", imageUrl = "https://example.com/calves.jpg")
        )
    )

    private val bodyEntities = listOf(
        BodyEntity(name = "Chest", imageUrl = "https://example.com/chest.jpg"),
        BodyEntity(name = "Back", imageUrl = "https://example.com/back.jpg"),
        BodyEntity(name = "Calves", imageUrl = "https://example.com/calves.jpg")
    )

    @Test
    fun `getBodyParts fetches data from remote source and caches it`() {
        testScope.runTest {
            // Given
            val fakeDtoData = bodyResponseDTO
            coEvery { remoteSource.fetchBodyParts() } returns fakeDtoData

            val fakeEntities = bodyEntities
            coEvery { localSource.getAll() } returns bodyEntities

            val repository = BodyRepositoryImpl(
                bodyLocalSource = localSource,
                bodyRemoteSource = remoteSource
            )

            // When
            val result = repository.getBodyParts(false)

            // Then
            assert(result == fakeEntities.map {
                it.toDomain()
            })
        }
    }

    @Test
    fun `watchBodyParts emits data from local source`() {
        testScope.runTest {
            // Given
            val fakeEntities = bodyEntities
            // Mocking watchAll() to return a Flow containing the list of entities
            every { localSource.watchAll() } returns flowOf(fakeEntities)

            val repository = BodyRepositoryImpl(
                bodyLocalSource = localSource,
                bodyRemoteSource = remoteSource
            )

            // When
            val resultFlow = repository.watchBodyParts()
            val result = resultFlow.first()

            // Then
            assert(result == fakeEntities.map { it.toDomain() })
        }
    }

}