package com.codewithmisu.fitatlas.exercise.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseLocalSource {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveExercises(exercises: List<ExerciseEntity>)

    @Query("SELECT * FROM table_exercise WHERE bodyPart LIKE :bodyPart")
    suspend fun getExercises(bodyPart: String): List<ExerciseEntity>

    @Query("SELECT * FROM table_exercise WHERE bodyPart LIKE :bodyPart")
    fun watchExercises(bodyPart: String): Flow<List<ExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveExerciseDetail(exerciseDetail: ExerciseDetailEntity)

    @Query("SELECT * FROM table_exercise_detail WHERE exerciseId = :exerciseId")
    suspend fun getExerciseDetail(exerciseId: String): ExerciseDetailEntity?
}