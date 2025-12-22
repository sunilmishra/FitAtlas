package com.codewithmisu.fitatlas.exercise.domain

data class Exercise(
    val exerciseId: String,
    val name: String,
    val imageUrl: String,
)

data class ExerciseDetail(
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
