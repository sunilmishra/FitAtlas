package com.codewithmisu.fitatlas.exercise.data

data class ExerciseResponseDTO(
    val success: Boolean,
    val data: List<ExerciseDTO>
)

data class ExerciseDTO(
    val exerciseId: String,
    val name: String,
    val imageUrl: String,
)

data class ExerciseDetailResponseDTO(
    val success: Boolean,
    val data: ExerciseDetailDTO
)

data class ExerciseDetailDTO(
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

fun ExerciseDTO.toEntity(): ExerciseEntity {
    return ExerciseEntity(
        exerciseId = exerciseId,
        name = name,
        imageUrl = imageUrl
    )
}

fun ExerciseDetailDTO.toEntity(): ExerciseDetailEntity {
    return ExerciseDetailEntity(
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


