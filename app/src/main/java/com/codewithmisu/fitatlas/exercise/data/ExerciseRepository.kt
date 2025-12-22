package com.codewithmisu.fitatlas.exercise.data

import com.codewithmisu.fitatlas.exercise.domain.Exercise
import com.codewithmisu.fitatlas.exercise.domain.ExerciseDetail
import com.codewithmisu.fitatlas.exercise.domain.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExerciseRepositoryImpl @Inject constructor(
    val localSource: ExerciseLocalSource,
    val remoteSource: ExerciseRemoteSource
) : ExerciseRepository {

    override suspend fun getExercises(bodyPart: String, forceRefresh: Boolean): List<Exercise> {
        val exerciseEntities = localSource.getExercises(bodyPart) as MutableList
        if (exerciseEntities.isEmpty() || forceRefresh) {
            val response = remoteSource.fetchExercises(bodyPart)
            val exerciseList = response.data
            val entities = exerciseList.map {
                it.toEntity().copy(bodyPart = bodyPart)
            }
            exerciseEntities.clear()
            exerciseEntities.addAll(entities)
            localSource.saveExercises(exerciseEntities)
        }
        return exerciseEntities.map {
            it.toDomain()
        }
    }

    override fun watchExercises(bodyPart: String): Flow<List<Exercise>> {
        return localSource.watchExercises(bodyPart).map {
            it.map { entity ->
                entity.toDomain()
            }
        }
    }

    override suspend fun getExerciseDetail(
        exerciseId: String,
        forceRefresh: Boolean
    ): ExerciseDetail? {
        var exerciseDetailEntity = localSource.getExerciseDetail(exerciseId)
        if (exerciseDetailEntity == null || forceRefresh) {
            val response = remoteSource.fetchExerciseDetail(exerciseId)
            val entity = response.data.toEntity()
            localSource.saveExerciseDetail(entity)
            exerciseDetailEntity = localSource.getExerciseDetail(exerciseId)
        }
        return exerciseDetailEntity?.toDomain()
    }
}