package com.codewithmisu.fitatlas.exercise.domain

import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {

    suspend fun getExercises(bodyPart: String,  forceRefresh: Boolean = false): List<Exercise>

    fun watchExercises(bodyPart: String): Flow<List<Exercise>>

    suspend fun getExerciseDetail(exerciseId: String, forceRefresh: Boolean = false): ExerciseDetail?
}