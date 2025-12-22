package com.codewithmisu.fitatlas.exercise.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.codewithmisu.fitatlas.exercise.domain.Exercise
import com.codewithmisu.fitatlas.exercise.domain.ExerciseDetail

@Entity("table_exercise")
data class ExerciseEntity(
    @PrimaryKey
    val exerciseId: String,
    // Link from Body Part, so we can read entities based on it
    val bodyPart: String? = null,
    val name: String,
    val imageUrl: String,
)

@Entity(tableName = "table_exercise_detail")
data class ExerciseDetailEntity(
    @PrimaryKey
    val exerciseId: String,
    val name: String,
    val imageUrl: String,
    val equipments: List<String>,
    val bodyParts: List<String>,
    val exerciseType: String,
    val targetMuscles: List<String>,
    val secondaryMuscles: List<String>,
    val videoUrl: String,
    val keywords: List<String>,
    val overview: String,
    val instructions: List<String>,
    val exerciseTips: List<String>,
    val variations: List<String>,
    val relatedExerciseIds: List<String>
)

fun ExerciseEntity.toDomain(): Exercise {
    return Exercise(
        exerciseId = exerciseId,
        name = name,
        imageUrl = imageUrl
    )
}

fun ExerciseDetailEntity.toDomain(): ExerciseDetail {
    return ExerciseDetail(
        exerciseId = exerciseId,
        name = name,
        imageUrl = imageUrl,
        equipments = equipments,
        bodyParts = bodyParts,
        exerciseType = exerciseType,
        targetMuscles = targetMuscles,
        secondaryMuscles = secondaryMuscles,
        videoUrl = videoUrl,
        keywords = keywords,
        overview = overview,
        instructions = instructions,
        exerciseTips = exerciseTips,
        variations = variations,
        relatedExerciseIds = relatedExerciseIds
    )
}


